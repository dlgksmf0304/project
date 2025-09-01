package org.mbc.czo.function.cart.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.mbc.czo.function.common.entity.BaseTimeEntity;
import org.mbc.czo.function.member.domain.Member;

import java.util.ArrayList;
import java.util.List;

@Entity // JPA 엔티티로 지정 (DB 테이블과 매핑됨)
@Getter
@Setter
public class Cart { // 장바구니
    // 회원정보랑 주문번호

    @Id // 기본키 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;
    // 회원 한 명이 갖는 장바구니를 구분하기 위한 id
    // ex) 회원의 장바구니 조회
    // ex) 장바구니에 담긴 cartItem관리
    // ex) 장바구니 비우기, 삭제, 수정 변경 시 참조

    @OneToOne // 회원(Member)과 1:1 관계 (한 회원당 한 장바구니)
    @JoinColumn(name = "member_id")
    // 외래키(FK) 컬럼: cart 테이블에 member_id 생성
    private Member member;

    // Cart ↔ CartItem 양방향 매핑
    @OneToMany(
            mappedBy = "cart",              // CartItem 엔티티의 cart 필드와 매핑됨
            cascade = CascadeType.ALL,      // Cart 저장/삭제 시 CartItem도 같이 처리됨
            orphanRemoval = true            // 고아 객체 자동 제거 (리스트에서 빠지면 DB에서도 삭제)
    )
    private List<CartItem> cartItems = new ArrayList<>();

    // 장바구니 생성 메서드 (정적 팩토리 메서드) - 회원 한명당 생기는 장바구니
    public static Cart createCart(Member member) {
        Cart cart = new Cart();
        cart.setMember(member); // 특정 회원과 연결된 장바구니 생성
        return cart;
    }
}
