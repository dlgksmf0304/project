package org.mbc.czo.function.boarduser.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mbc.czo.function.boardAdmin.domain.BoardAdmin;
import org.mbc.czo.function.boardAdmin.dto.BoardAdminDTO;
import org.mbc.czo.function.boardAdmin.dto.PageAdminRequestDTO;
import org.mbc.czo.function.boarduser.domain.Board;
import org.mbc.czo.function.boarduser.dto.BoardAllList;
import org.mbc.czo.function.boarduser.dto.BoardDTO;
import org.mbc.czo.function.boarduser.dto.PageRequestDTO;
import org.mbc.czo.function.boarduser.dto.PageResponseDTO;
import org.mbc.czo.function.boarduser.repository.BoardRepository;
import org.mbc.czo.function.boarduser.service.BoardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/board")
@Log4j2
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final BoardRepository boardRepository;

    @GetMapping("/userList")
    public void userList(PageRequestDTO pageRequestDTO, Model model, Pageable pageable) {

        //PageResponseDTO<BoardDTO> responseDTO1 = boardService.list(pageRequestDTO);

        PageResponseDTO<BoardAllList> responseDTO1 =
                boardService.listWithAll(pageRequestDTO);

           log.info(responseDTO1);

        Page<Board> result = boardRepository.findAllOrderByNotice(pageable);
        model.addAttribute("responseDTO1", responseDTO1);
        model.addAttribute("dtoList", responseDTO1.getDtoList());
        model.addAttribute("pageRequestDTO", pageRequestDTO);

    }

    @GetMapping("/userRegister")
    public String userRegister(Model model) {
        model.addAttribute("dto", new BoardDTO());
        return "board/userRegister";
    }

    @PostMapping("/userRegister")
    public String userRegister(@Valid BoardDTO boardDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        log.info("보드 포스트 레지스터................");
        if (bindingResult.hasErrors()){
            log.info("하스 에러......");
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/board/userRegister";
        }
        log.info(boardDTO);
        Long bno = boardService.register(boardDTO);
        redirectAttributes.addFlashAttribute("result", bno);
        return "redirect:/board/userList";
    }

    @GetMapping("/userRead")
    public String userRead(@RequestParam(required=false) Long bno,
                       PageRequestDTO pageRequestDTO,
                       Model model) {
        if (bno == null) {
            // bno가 없으면 목록 페이지로 리다이렉트하거나 기본 처리
            return "redirect:/board/userList";
        }

        BoardDTO boardDTO = boardService.readOne(bno);
        model.addAttribute("dto", boardDTO);
        model.addAttribute("PageRequestDTO", pageRequestDTO);

        return "board/userRead";
    }

    @GetMapping("/userModify")
    public String userModify(Long bno, Model model, PageRequestDTO pageRequestDTO) {
        BoardDTO boardDTO = boardService.readOne(bno);
        log.info(boardDTO);
        model.addAttribute("dto", boardDTO);

        return "board/userModify";
    }

    @PostMapping("/userModify")
    public String userModify(PageRequestDTO pageRequestDTO, @Valid BoardDTO boardDTO,
                             BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        log.info("게시판 수정 중...."+boardDTO);

        if(bindingResult.hasErrors()){
            log.info("error....");

            String link = pageRequestDTO.getLink();

            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());

            redirectAttributes.addFlashAttribute("bno", boardDTO.getBno());

            return "redirect:/board/userModify"+link;
        }

        boardService.modify(boardDTO);

        redirectAttributes.addFlashAttribute("result", "modified");

        redirectAttributes.addAttribute("bno", boardDTO.getBno());

        return "redirect:/board/userRead";
    }

    @PostMapping("/userRemove")
    public String userRemove (Long bno, RedirectAttributes redirectAttributes) {

        log.info("remove ... "+bno);

        boardService.remove(bno);

        redirectAttributes.addFlashAttribute("result", "removed");

        return "redirect:/board/useList";
    }



}
