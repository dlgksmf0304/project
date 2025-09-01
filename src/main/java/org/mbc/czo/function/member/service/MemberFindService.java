package org.mbc.czo.function.member.service;

import org.mbc.czo.function.member.dto.MemberFindIDDTO;
import org.mbc.czo.function.member.dto.MemberFindPWDTO;

public interface MemberFindService {

    // 아이디 찾기 Exception
    static class M_FindException extends Exception {

        // 만일 아이디가 존재안하면 예외를 발생
    }

    public String findID(MemberFindIDDTO memberFindIDDTO)throws M_FindException;
    public String findPW(MemberFindPWDTO memberFindPWDTO) throws M_FindException;
}
