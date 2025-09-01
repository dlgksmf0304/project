package org.mbc.czo.function.image.repository;

import org.mbc.czo.function.image.domain.MemberProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberProfileImageJpaRepository extends JpaRepository<MemberProfileImage, Long> {

    //MemberProfileImage.member.mid 조회
    Optional<MemberProfileImage> findByMember_Mid(String memberId);
}
