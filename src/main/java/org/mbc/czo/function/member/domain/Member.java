package org.mbc.czo.function.member.domain;

import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.mbc.czo.function.image.domain.MemberProfileImage;
import org.mbc.czo.function.member.constant.Role;
import org.mbc.czo.function.member.dto.MemberJoinDTO;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name="member")
@Getter
@Setter
@Builder
@ToString

public class Member {

    @Id
    @NotNull
    @Column(name="member_id", length = 255)
    private String mid;

    @NotNull
    @Column(name="member_name", length = 255)
    private String mname;

    @NotNull
    @Column(name="member_phoneNumber", length = 11, unique = true)  // 유니크 처리
    private String mphoneNumber;

    @NotNull
    @Column(name="member_email", length = 30, unique = true)  // 유니크 처리
    private String memail;  // 회원 검색 처리용

    @NotNull
    @Column(name="member_password", length = 255) //암호화 시 글자 수 늘어남에 따른 length 증가
    private String mpassword;

    @Column(name="member_postcode", length = 100)
    private String mpostcode;

    @Column(name="member_address", length = 100)
    private String maddress;

    @Column(name="member_detailAddress", length = 100)
    private String mdetailAddress;

    // 회원롤 관리(user,admin)
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<MemberRole> mroleSet = new HashSet<>();

    @Column(name="member_mileage")
    private Long mmileage;

    //mappedBy는 **양방향 연관관계에서 "주인이 아닌 쪽"**을 나타냄
    //MemberProfileImage가 주인(Owner)
    //매핑 정보를 주인쪽(MemberProfileImage.member)에서 가져온다는 뜻, 그러므로 db테이블에는 안생김

    //fetch = FetchType.LAZY 지연 로딩
    //Member를 조회할 때 바로 profileImage를 불러오지 않고, 실제로 접근할 때 DB에서 가져옴
    //MODIFY에서만 생성되므로 생성자에 X
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private MemberProfileImage profileImage;

    @Column(name = "member_isActivate")
    private boolean misActivate;

    @Column(name = "member_isSocialActivate")
    private boolean misSocialActivate;

    public void addRole(Role role) {
        MemberRole memberRole = MemberRole.builder()
                .role(role)
                .member(this)
                .build();
        this.mroleSet.add(memberRole);
    }

    public Set<String> getRoleNames() {
        return mroleSet.stream()
                .map(memberRole -> memberRole.getRole().name())
                .collect(Collectors.toSet());
    }

    @PrePersist //db 저장 전 실행(초기화)
    public void prePersist() {
        this.misActivate = true;
    }

    public static Member createMember(MemberJoinDTO memberJoinDTO, PasswordEncoder passwordEncoder) {
        return Member.builder()
                .mid(memberJoinDTO.getMid())
                .mname(memberJoinDTO.getMname())
                .mphoneNumber(memberJoinDTO.getMphoneNumber())
                .memail(memberJoinDTO.getMemail())
                .mpassword(passwordEncoder.encode(memberJoinDTO.getMpassword()))
                .mpostcode(memberJoinDTO.getMpostcode())
                .maddress(memberJoinDTO.getMaddress())
                .mdetailAddress(memberJoinDTO.getMdetailAddress())
                .mmileage(memberJoinDTO.getMmileage())
                .misSocialActivate(memberJoinDTO.isMisSocialActivate())
                .build();
    }

    public void updateMember(MemberJoinDTO memberModifyDTO, PasswordEncoder passwordEncoder) {
        this.mid = memberModifyDTO.getMid();
        this.mname = memberModifyDTO.getMname();
        this.mphoneNumber = memberModifyDTO.getMphoneNumber();
        this.memail = memberModifyDTO.getMemail();
        this.mpassword = passwordEncoder.encode(memberModifyDTO.getMpassword());
        this.mpostcode = memberModifyDTO.getMpostcode();
        this.maddress = memberModifyDTO.getMaddress();
        this.mdetailAddress = memberModifyDTO.getMdetailAddress();
        this.mmileage = memberModifyDTO.getMmileage();
    }

}
