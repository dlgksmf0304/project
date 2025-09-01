package org.mbc.czo.function.member.domain;

import jakarta.persistence.*;
import lombok.*;
import org.mbc.czo.function.member.constant.Role;

@Entity
@Table(name = "member_role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)  // 문자열로 저장
    @Column(nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")  // FK: member 테이블
    private Member member;
}

