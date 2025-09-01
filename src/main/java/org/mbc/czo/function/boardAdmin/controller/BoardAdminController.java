package org.mbc.czo.function.boardAdmin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mbc.czo.function.boardAdmin.domain.BoardAdmin;
import org.mbc.czo.function.boardAdmin.dto.BoardAdminDTO;
import org.mbc.czo.function.boardAdmin.dto.PageAdminRequestDTO;
import org.mbc.czo.function.boardAdmin.dto.PageAdminResponseDTO;
import org.mbc.czo.function.boardAdmin.repository.BoardAdminRepository;
import org.mbc.czo.function.boardAdmin.service.BoardAdminLikeService;
import org.mbc.czo.function.boardAdmin.service.BoardAdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/board")
@Log4j2
@RequiredArgsConstructor
public class BoardAdminController {

    private final BoardAdminService boardAdminService;
    private final BoardAdminRepository boardAdminRepository;
    private final BoardAdminLikeService boardAdminLikeService;

    private PageAdminResponseDTO<BoardAdminDTO> getBoardList(PageAdminRequestDTO requestDTO ) {
        Pageable pageable = PageRequest.of(
                requestDTO.getPage() - 1,
                requestDTO.getSize(),
                Sort.by(Sort.Order.desc("notice"), Sort.Order.desc("bno"))  // 공지 박는 거 (번호 순서대로)
        );
        Page<BoardAdmin> result = boardAdminRepository.findAll(pageable);
        List<BoardAdminDTO> dtoList = result.getContent().stream()
                .map(BoardAdminDTO::new)
                .collect(Collectors.toList());
        return new PageAdminResponseDTO<BoardAdminDTO>(requestDTO, dtoList, (int)result.getTotalElements());
    }

    public List<BoardAdminDTO> getBoardList1(String username) {
        List<BoardAdmin> boards = boardAdminRepository.findAll();
        return boards.stream().map(board -> {
            BoardAdminDTO dto = new BoardAdminDTO(board);
            dto.setLiked(boardAdminLikeService.isLiked(board.getBno(), username));
            dto.setLikeCount(boardAdminLikeService.countLikes(board));
            return dto;
        }).collect(Collectors.toList());
    }

    @GetMapping("/list") // HTML
    public String listHtml(@ModelAttribute PageAdminRequestDTO requestDTO, Model model) {
        log.info("requestDTO.page={}, size={}", requestDTO.getPage(), requestDTO.getSize());

        PageAdminResponseDTO<BoardAdminDTO> responseDTO = getBoardList(requestDTO);
        model.addAttribute("responseDTO", responseDTO);
        return "board/list";
    }

    @GetMapping("/api/list") // REST API
    @ResponseBody
    public PageAdminResponseDTO<BoardAdminDTO> listApi(@ModelAttribute PageAdminRequestDTO requestDTO) {
        return getBoardList(requestDTO);
    }

    @GetMapping("/list1")
    public String list(Model model, @AuthenticationPrincipal UserDetails user,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("notice"), Sort.Order.desc("bno")));

        List<BoardAdminDTO> dtos = boardAdminService.getBoardList(pageable);

        String username = (user != null) ? user.getUsername() : null;
        dtos.forEach(dto -> dto.setLiked(boardAdminLikeService.isLiked(dto.getBno(), username)));

        model.addAttribute("dtoList", dtos);
        return "board/list";
    }


    @GetMapping("/register")
        public String registerGet(Model model) {
            model.addAttribute("dto", new BoardAdminDTO());
            return "board/register";
    }

    @PostMapping("/register")
    public String registerPost(@Valid BoardAdminDTO boardAdminDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, Principal principal){
        /*boardAdminDTO.setWriter(principal.getName());  // 로그인한 작성자로 고정*/
        boardAdminDTO.setWriter("리라이프");
        log.info("글 등록 시도, DTO: {}", boardAdminDTO);
        // DTO 값 확인
        if(boardAdminDTO.getWriter() == null || boardAdminDTO.getContent() == null) {
            log.error("DTO 값이 없음!");
            return "redirect:/board/register?error";
        }
        boardAdminService.save(boardAdminDTO);
        log.info("글 등록 성공");
        return "redirect:/board/list";
    }

    @GetMapping("/read")
    public String read(@RequestParam(required=false, name = "bno") Long bno,
                       PageAdminRequestDTO pageAdminRequestDTO,
                       Model model) {
        if(bno == null) {
            // bno가 없으면 목록 페이지로 리다이렉트하거나 기본 처리
            return "redirect:/board/list";
        }

        BoardAdminDTO boardAdminDTO = boardAdminService.readOne(bno);
        model.addAttribute("dto", boardAdminDTO);
        model.addAttribute("PageAdminRequestDTO", pageAdminRequestDTO);

        return "board/read";
    }

    @GetMapping("/read/{bno}")
    public String read(@PathVariable Long bno, Model model, PageAdminRequestDTO pageAdminRequestDTO) {
        BoardAdmin dto = boardAdminService.getBoard(bno);
        model.addAttribute("dto", dto);
        model.addAttribute("pageAdminRequestDTO", pageAdminRequestDTO);
        return "board/read";  // Thymeleaf 템플릿
    }

    @GetMapping("/modify")
    public String modifyForm(@RequestParam("bno") Long bno, Model model) {
        BoardAdminDTO dto = boardAdminService.get(bno);
        model.addAttribute("dto", dto);
        return "board/modify"; // modify.html
    }
    @PostMapping("/modify")
    public String modify(PageAdminRequestDTO pageAdminRequestDTO, @Valid BoardAdminDTO boardAdminDTO,
                         BindingResult bindingResult, RedirectAttributes redirectAttributes){
        log.info("보드 수정 포스트....." + boardAdminDTO);
        if(bindingResult.hasErrors()){
            log.info("에러,,");
            String link = pageAdminRequestDTO.getLink();
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("bno", boardAdminDTO.getBno());
            return "redirect:/board/modify?"+link;
        }
        boardAdminService.modify(boardAdminDTO);
        redirectAttributes.addFlashAttribute("result", "modified");
        redirectAttributes.addFlashAttribute("bno", boardAdminDTO.getBno());
        return "redirect:/board/read?bno=" + boardAdminDTO.getBno();
    }

    @PostMapping("/remove")
    public String remove(Long bno, RedirectAttributes redirectAttributes){
        log.info("지운다 포스트.." + bno);
        boardAdminService.remove(bno);
        redirectAttributes.addFlashAttribute("result", "removed");
        return "redirect:/board/list";
    }

    @GetMapping("/access-denied")  // 페이지 유저일 때도 리턴 가능하게
    public String accessDenied() {
        return "accessDenied"; // templates/accessDenied.html
    }



}
