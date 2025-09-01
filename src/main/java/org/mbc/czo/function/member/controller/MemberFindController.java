package org.mbc.czo.function.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mbc.czo.function.member.domain.Member;
import org.mbc.czo.function.member.dto.MemberFindIDDTO;
import org.mbc.czo.function.member.dto.MemberFindPWDTO;

import org.mbc.czo.function.member.service.MemberFindServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Log4j2
@Controller
@RequiredArgsConstructor // final을 붙인 필드로 생성자 만듬.

//member의 검색(search) 로직 담당
public class MemberFindController {

    private final MemberFindServiceImpl memberFindServiceImpl;


    @GetMapping("/member/findID")
    public void get_MemberfindID() {
        log.info("MemberFindServiceImpl.get_MemberfindID 실행....");
    }

    @PostMapping("/member/findID")
    public String post_MemberfindID(MemberFindIDDTO memberFindIDDTO, RedirectAttributes redirectAttributes){
        // html에서 넘어오는 데이터 처리용

        log.info("MemberFindServiceImpl.post_MemberfindID..... ");
        log.info(memberFindIDDTO);


        try{
            String search_m_id = memberFindServiceImpl.findID(memberFindIDDTO); // 아이디 찾기
            log.info("찾은 아이디: {}", search_m_id);
            // 성공 메시지와 아이디 전달
            redirectAttributes.addFlashAttribute("findID", search_m_id);
            redirectAttributes.addFlashAttribute("message", "회원님의 아이디를 찾았습니다.");
            redirectAttributes.addFlashAttribute("status", "success");

        } catch (MemberFindServiceImpl.M_FindException e) {
            log.warn("아이디를 찾지 못했습니다.");

            // 실패 메시지 전달
            redirectAttributes.addFlashAttribute("message", "입력하신 정보로 가입된 아이디가 없습니다.");
            redirectAttributes.addFlashAttribute("status", "error");
            return "redirect:/member/findID";
        }
        return "redirect:/member/findID";
    }

    @GetMapping("/member/findPW")
    public void get_MemberfindPW() {
        log.info("MemberFindServiceImpl.get_MemberfindPW 실행....");
    }

    @PostMapping("/member/findPW")
    public String post_MemberfindPW(MemberFindPWDTO memberFindPWDTO, RedirectAttributes redirectAttributes){
        // html에서 넘어오는 데이터 처리용

        log.info("MemberFindServiceImpl.post_MemberfindPW..... ");
        log.info(memberFindPWDTO);

        try{
            String changePWAccountID = memberFindServiceImpl.findPW(memberFindPWDTO); // 아이디 찾기

            redirectAttributes.addFlashAttribute("changePWAccountID", changePWAccountID);
            // 성공 메시지와 아이디 전달
           /* redirectAttributes.addFlashAttribute("findPW", search_m_pw);
            redirectAttributes.addFlashAttribute("message", "회원님의 비밀번호를 찾았습니다.");
            redirectAttributes.addFlashAttribute("status", "success");*/

        } catch (MemberFindServiceImpl.M_FindException e) {
            log.warn("비밀번호를 찾지 못했습니다.");

            // 실패 메시지 전달
            redirectAttributes.addFlashAttribute("message", "입력하신 정보로 가입된 계정이 없습니다.");
            redirectAttributes.addFlashAttribute("status", "error");
            return "redirect:/member/findPW";
        }
        return "redirect:/member/resetPW";
    }

}
