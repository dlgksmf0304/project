package org.mbc.czo.function.member.service;

import org.mbc.czo.function.member.dto.MemberJoinDTO;
import org.mbc.czo.function.member.dto.MemberResetPWDTO;

public interface MemberAuthService {

    //중복 아이디 처리 Exception
    static class M_AuthException extends Exception {

        // 만일 같은 아이디가 존재하면 예외를 발생
    }

    public void join(MemberJoinDTO memberJoinDTO)throws M_AuthException;
    public void resetPW(MemberResetPWDTO memberResetPWDTO, String changePWAccountID)throws M_AuthException;
    public void modify(String username, MemberJoinDTO membermodifyDTO)throws M_AuthException;
    public void delete(String username, String checkPassword)throws M_AuthException;
}
