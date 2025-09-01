package org.mbc.czo.function.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mbc.czo.function.member.dto.MemberFindIDDTO;
import org.mbc.czo.function.member.dto.MemberFindPWDTO;
import org.mbc.czo.function.member.repository.MemberJpaRepository;


import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@Service("MemberFindService")
@RequiredArgsConstructor // final붙은 필드를 생성자로
public class MemberFindServiceImpl implements MemberFindService {

    private final MemberJpaRepository memberJpaRepository;  // member db 처리용

    public String findID(MemberFindIDDTO memberFindIDDTO) throws M_FindException {

        log.info("=============================");
        log.info("MemberServiceImpl.findID");

        String m_name = memberFindIDDTO.getMname();
        String m_phoneNumber = memberFindIDDTO.getMphoneNumber();
        String resultID = memberJpaRepository.checkExistIDFromNameAndPhoneNumber(m_name,m_phoneNumber);

        if(resultID == null || resultID.isEmpty()){
            // 해당하는 정보가 db에 없으면
            throw new M_FindException();
        }
        log.info(resultID);

        return  resultID;
    }

    public String findPW(MemberFindPWDTO memberFindPWDTO) throws M_FindException {

        log.info("=============================");
        log.info("MemberServiceImpl.findPW");

        String m_id = memberFindPWDTO.getMid();
        String m_name = memberFindPWDTO.getMname();

        String resultID = memberJpaRepository.checkExistPWFromIDAndName(m_id,m_name);

        if(resultID == null || resultID.isEmpty()){
            // 해당하는 정보가 db에 없으면
            throw new M_FindException();
        }
        log.info(resultID);

        return resultID;
    }


}
