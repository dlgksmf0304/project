package org.mbc.czo.function.cart.repository;

import org.mbc.czo.function.cart.domain.CartItem;
import org.mbc.czo.function.cart.dto.CartDetailDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // 1. 장바구니에 특정 상품이 이미 담겨있는지 확인
    // cartId와 itemId로 CartItem을 조회
    // Optional<CartItem> 반환 → 있으면 장바구니 항목 존재, 없으면 비어있음
    Optional<CartItem> findByCart_IdAndItem_Id(Long cartId, Long itemId);

    // 2. 장바구니 상세 정보를 DTO로 조회
    @Query("SELECT new org.mbc.czo.function.cart.dto.CartDetailDTO(" +
            "ci.id, i.itemNm, i.price, ci.count, im.imgUrl) " +  // CartDetailDTO 생성자 호출
            "FROM CartItem ci " +                                  // CartItem 엔티티 기준
            "JOIN ci.item i " +                                   // CartItem과 연관된 Item 조인
            "JOIN ItemImg im ON im.item.id = i.id " +            // ItemImg와 조인
            "WHERE ci.cart.id = :cartId " +                     // 특정 장바구니 필터링
            "AND im.repimgYn = 'Y' " +                          // 대표 이미지(repimgYn='Y')만 선택
            "ORDER BY ci.regTime DESC")                          // 등록 시간 기준 내림차순 정렬
    List<CartDetailDTO> findCartDetailDTOList(Long cartId);

    // 3. 특정 회원 장바구니 상품 개수 조회
    // Cart → Member → memail 순으로 연결하여 count 계산
    // 예: 헤더 장바구니 갯수 표시
    long countByCart_Member_Memail(String email);


}
