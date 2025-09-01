package org.mbc.czo.function.cart.repository;

import org.mbc.czo.function.cart.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    // Member의 이메일로 장바구니 조회 (로그인한 회원 기준으로 장바구니 찾을때 사용)
    // ex) 사용자가 '장바구니' 버튼을 눌렀을때 member.memail을 기준으로 DB에서 해당 회원의 장바구니 조회
    Optional<Cart> findByMemberMemail(String memail);

}
