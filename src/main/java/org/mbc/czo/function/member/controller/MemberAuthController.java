package org.mbc.czo.function.member.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mbc.czo.function.member.dto.MemberJoinDTO;
import org.mbc.czo.function.member.dto.MemberResetPWDTO;

import org.mbc.czo.function.member.service.MemberAuthServiceImpl;
import org.mbc.czo.function.member.service.MemberAuthService;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Log4j2
@Controller
@RequiredArgsConstructor // final을 붙인 필드로 생성자 만듬.

//member의 인증(Authentication) 로직 담당
public class MemberAuthController {

    private final MemberAuthService memberAuthService;

    @GetMapping("/member/login")
    public void get_MemberLogin(String error, String logout) {

        // http://localhost/member/login?error=???
        // http://localhost/member/login?logout=???
        log.info("MemberAuthController.loginGet메서드 실행....");
        log.info("logout: " + logout); //데이터베이스에서 활용
        log.info("error: " + error); //데이터베이스에서 활용

        if(logout != null){
            log.info("logout 처리됨!!! : " + logout);
        }
    }

    //emailcontroller는 mamber/email폴더에
    @GetMapping("/member/join")
    public String get_MemberJoin(Model model) {
        // Thymeleaf에서 사용될 boolean 값 기본 세팅
        if (!model.containsAttribute("emailSent")) {
            model.addAttribute("emailSent", false);
        }
        if (!model.containsAttribute("emailVerified")) {
            model.addAttribute("emailVerified", false);
        }
        return "member/join";
    }

    @PostMapping("/member/join")
    public String post_MemberJoin(MemberJoinDTO memberJoinDTO, RedirectAttributes redirectAttributes){
        // html에서 넘어오는 데이터 처리용

        log.info("MemberAuthController.post_MemberJoin..... ");
        log.info(memberJoinDTO);

        try{
            memberAuthService.join(memberJoinDTO); // 회원가입 처리됨!!

            //id 중복되면 MidExistException예외처리 발생
        }catch(MemberAuthServiceImpl.M_AuthException e){

            //redirectAttributes.addFlashAttribute("error","mid");
            // 회원가입시 id 중복되는 예외처리
            return "redirect:/member/join"; // 회원가입 페이지로 다시 감.

        }
        //redirectAttributes.addFlashAttribute("result","success");
        return "redirect:/member/login"; //회원가입 성공시 로그인 페이지로 이동
    }

    @GetMapping("/member/resetPW")
    public String get_MemberResetPW() {
        return "member/resetPW";
    }

    @PostMapping("/member/resetPW")
    public String post_MemberResetPW(
            MemberResetPWDTO memberResetPWDTO,
            @RequestParam("changePWAccountID") String changePWAccountID,
            RedirectAttributes redirectAttributes){

        log.info("MemberAuthController.post_MemberResetPW..... ");
        log.info(memberResetPWDTO);

        try{
            memberAuthService.resetPW(memberResetPWDTO, changePWAccountID);

        }catch(MemberAuthServiceImpl.M_AuthException e){

            // 실패 메시지 전달
            redirectAttributes.addFlashAttribute("changePWAccountID", changePWAccountID);
            redirectAttributes.addFlashAttribute("message", "새로운 비밀번호와 비밀번호 확인이 다릅니다");
            redirectAttributes.addFlashAttribute("status", "error");
            return "redirect:/member/resetPW"; //실패

        }

        return "redirect:/"; //성공
    }
    @PreAuthorize("isAuthenticated()") // 로그인한 상태이면!!! (권한에 상관없음)
    @GetMapping("/member/modify")
    public String get_MemberModify() {
        log.info("MemberAuthController.get_MemberModify..... ");
        return "member/modify";
    }

    @PreAuthorize("isAuthenticated()") // 로그인한 상태이면!!! (권한에 상관없음)
    @PostMapping("/member/modify")
    public String post_MemberModify(
            MemberJoinDTO membermodifyDTO,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        log.info("MemberAuthController.post_MemberModify..... ");

        // 로그인 사용자 확인
        String username = principal.getName();

        try {
            memberAuthService.modify(username, membermodifyDTO);

        } catch(MemberAuthServiceImpl.M_AuthException e) {

            log.info("MemberAuthController.post_MemberModify modify실패 M_AuthException");
            return "redirect:/member/modify"; // 실패
        }

        return "redirect:/"; // 성공
    }

    @PreAuthorize("isAuthenticated()") // 로그인한 상태이면!!! (권한에 상관없음)
    @GetMapping("/member/delete")
    public String get_MemberDelete() {
        log.info("MemberAuthController.delete..... ");
        return "member/delete";
    }

    @PreAuthorize("isAuthenticated()") // 로그인한 상태이면!!! (권한에 상관없음)
    @PostMapping("/member/delete")
    public String Post_MemberDelete(@RequestParam String checkPassword,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes,
                                   HttpSession session) {
        // 로그인 사용자 확인
        String username = principal.getName();

        log.info("MemberAuthController.Post_MemberDelete..... ");
        log.info(username);
        // 비밀번호 검증

        try{
            // 계정 삭제
            memberAuthService.delete(username, checkPassword);

        }catch(MemberAuthServiceImpl.M_AuthException e){

            // 실패 메시지 전달
            redirectAttributes.addFlashAttribute("message", "비밀번호와 비밀번호 확인이 다릅니다");
            return "redirect:/member/delete"; //실패

        }

        // 로그아웃 처리
        session.invalidate();

        return "redirect:/member/goodbye"; // 삭제 후 이동할 페이지
    }

    @PreAuthorize("isAuthenticated()") // 로그인한 상태이면!!! (권한에 상관없음)
    @GetMapping("/member/userMyPage")
    public String get_UserMyPage() {
        log.info("MemberAuthController.get_UserMyPage..... ");
        return "member/userMyPage";
    }

}
