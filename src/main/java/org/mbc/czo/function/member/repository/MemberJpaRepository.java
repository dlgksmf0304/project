package org.mbc.czo.function.member.repository;

import org.mbc.czo.function.member.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member, String> {

    // id가 들어가면 해당 롤이 나옴
    @EntityGraph(attributePaths = "mroleSet") // 연관된 롤을 가져옴
    @Query("select m from Member m where m.mid = :member_id and m.misSocialActivate=false")
    Optional<Member> getWithRoles(@Param("member_id") String mid);

    // name phoneNumber로 id 있는지 체크
    @Query("select m.mid from Member m where m.mname = :member_name and m.mphoneNumber = :member_phoneNumber")
    String checkExistIDFromNameAndPhoneNumber(@Param("member_name") String m_name, @Param("member_phoneNumber") String m_phoneNumber);

    // name phoneNumber로 id 있는지 체크
    @Query("select m.mid from Member m where m.mid = :member_id and m.mname = :member_name")
    String checkExistPWFromIDAndName(@Param("member_id") String m_id, @Param("member_name") String m_name);

    @EntityGraph(attributePaths = "mroleSet")
    Optional<Member> findByMemail(String memail);
    // 이메일을 받아서 회원 정보를 가져옴.

    void deleteByMid(String mid);
}
