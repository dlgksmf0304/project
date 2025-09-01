package org.mbc.czo.function.member.security.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.mbc.czo.function.image.domain.MemberProfileImage;
import org.mbc.czo.function.member.constant.Role;
import org.mbc.czo.function.member.domain.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString

public class MemberSecurityDTO extends User implements OAuth2User {

    private String mid;

    private String mname;

    private String mphoneNumber;

    private String memail;  // 회원 검색 처리용

    private String mpassword;

    private String mpostcode;

    private String maddress;

    private String mdetailAddress;

    private Set<Role> mroleSet = new HashSet<Role>();

    private Long mmileage;

    private boolean misActivate;

    private boolean misSocialActivate;

    private Map<String, Object> mSocialprops;

    private String profileImagePath;

    // 생성자
    public MemberSecurityDTO(String username, String password, String email,
                             boolean del, boolean social, String mname,  String mphoneNumber
                             , String mpostcode , String maddress , String mdetailAddress, Long mmileage,
                             Collection<? extends GrantedAuthority> authorities) {

        super(username, password, authorities);  // User 객체

        this.mid = username;
        this.mpassword = password;
        this.memail = email;
        this.misActivate = del;
        this.misSocialActivate = social;
        this.mname = mname;
        this.mphoneNumber = mphoneNumber;
        this.mpostcode = mpostcode;
        this.maddress = maddress;
        this.mdetailAddress = mdetailAddress;
        this.mmileage = mmileage;
    }

    public static MemberSecurityDTO createMemberSecurityDTO(Member member, boolean misSocialActivate) {
        MemberSecurityDTO dto =  new MemberSecurityDTO(
                member.getMid(),
                member.getMpassword(),
                member.getMemail(),
                member.isMisActivate(),
                misSocialActivate,
                member.getMname(),
                member.getMphoneNumber(),
                member.getMpostcode(),
                member.getMaddress(),
                member.getMdetailAddress(),
                member.getMmileage(),
                member.getMroleSet().stream()
                        .map(memberRole -> new SimpleGrantedAuthority("ROLE_" + memberRole.getRole().name()))
                        .collect(Collectors.toList()));

        if(member.getProfileImage() != null){

            String relativePath = member.getProfileImage().getUploadPath(); // e.g. profile/uuid.png
            dto.setProfileImagePath("/uploads/" + relativePath); // 프론트에서 사용 가능 하게 하기위해 webconfig.ResourceHandler에서 세팅
        }

        return dto;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.getMSocialprops();
    }

    @Override
    public String getName() {
        return this.mid;
    }
}
