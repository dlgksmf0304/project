package org.mbc.czo.function.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mbc.czo.function.member.constant.Role;
import org.mbc.czo.function.member.domain.Member;
import org.mbc.czo.function.member.dto.MemberJoinDTO;
import org.mbc.czo.function.member.dto.MemberResetPWDTO;
import org.mbc.czo.function.member.repository.MemberJpaRepository;
import org.mbc.czo.function.member.security.dto.MemberSecurityDTO;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Log4j2
@Service("MemberAuthService")
@RequiredArgsConstructor // final붙은 필드를 생성자로

public class MemberAuthServiceImpl implements MemberAuthService {

    private final ModelMapper modelMapper;  // 엔티티를 dto변환
    private final MemberJpaRepository memberJpaRepository;  // member db 처리용
    private final PasswordEncoder passwordEncoder;    // 패스워드 암호화

    public void join(MemberJoinDTO memberJoinDTO) throws M_AuthException {

        String m_id = memberJoinDTO.getMid();
        boolean exist = memberJpaRepository.existsById(m_id); // 기존에 id 있는지 boolean return

        if(exist) {
            throw new M_AuthException(); // 중복id 처리용 예외처리 발생
        }

        // 진짜 회원가입처리
       /* Member member = modelMapper.map(memberJoinDTO, Member.class);
        // 엔티티                              dto

       // member.setM_password(memberJoinDTO.getM_password());
        member.setMpassword(passwordEncoder.encode(memberJoinDTO.getMpassword()));*/
        Member member =  Member.createMember(memberJoinDTO, passwordEncoder);
        member.addRole(Role.USER);

        log.info("==============join===============");
        log.info(memberJoinDTO);
        log.info(member.toString());

        memberJpaRepository.save(member);
    }

    public void resetPW(MemberResetPWDTO memberResetPWDTO, String changePWAccountID) throws M_AuthException {

        log.info("============resetPW=================");
        log.info(changePWAccountID);
        Optional<Member> result = memberJpaRepository.findById(changePWAccountID);

        if(result.isEmpty()){
            // 해당하는 정보가 db에 없으면
            //id 찾기 실패
            throw new M_AuthException();
        }

        Member member = result.get(); // 해당하는 member가 있으면 넣음

        log.info("============result.isEmpty()Clear=================");
        log.info(member.toString());

        String m_pw = memberResetPWDTO.getMpassword();
        String m_pwCheck = memberResetPWDTO.getMpasswordCheck();

        boolean check = m_pw.equals(m_pwCheck);

        if(!check) {
            throw new M_AuthException(); // 비밀번호 != 비밀번호확인
        }
        log.info("비밀번호 = 비밀번호 확인 통과");

        member.setMpassword(passwordEncoder.encode(m_pw));

        memberJpaRepository.save(member);


    }

    @Override
    public void modify(String username, MemberJoinDTO membermodifyDTO) throws M_AuthException {

        Optional<Member> result = memberJpaRepository.findById(username);

        if(result.isEmpty()){
            // 해당하는 정보가 db에 없으면
            //id 찾기 실패

            throw new M_AuthException();
        }

        Member member = result.get(); // 해당하는 member가 있으면 넣음
        member.updateMember(membermodifyDTO, passwordEncoder);
       /* Member member =  Member.createMember(membermodifyDTO, passwordEncoder);
        member.addRole(Role.USER);*/

        memberJpaRepository.save(member);

        // SecurityContext 갱신
        refreshAuthentication(member);
    }

    private void refreshAuthentication(Member member) {
        MemberSecurityDTO updatedDTO = MemberSecurityDTO.createMemberSecurityDTO(member, member.isMisSocialActivate());

        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                updatedDTO,
                updatedDTO.getPassword(),
                updatedDTO.getAuthorities()
        );
        //modify 이후 변경된 값을 SecurityContext에 저장 → 현재 로그인 상태의 인증 정보 갱신
        //즉 modify되도 세션에서 변경내용 감지
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    @Transactional
    @Override
    public void delete(String username, String checkPassword) throws M_AuthException {

        Optional<Member> result = memberJpaRepository.findById(username);
        log.info("delete service 시작");
        if(result.isEmpty()){
            // 해당하는 정보가 db에 없으면
            //id 찾기 실패
            log.info("id없음");
            throw new M_AuthException();
        }
        Member member = result.get(); // 해당하는 member가 있으면 넣음

        String memberPW = member.getMpassword();

        //최종 비밀번호 확인
        if(!checkPassword.equals(memberPW)
                && !passwordEncoder.matches(checkPassword,memberPW)){
            //비밀번호 확인 오류
            log.info("비번다름");
            throw new M_AuthException();
        }
        log.info("deleteByMid실행: " + username);
        memberJpaRepository.deleteByMid(username);
    }

}
