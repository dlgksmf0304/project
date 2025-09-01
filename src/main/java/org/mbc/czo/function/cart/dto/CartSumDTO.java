// dto/CartSummaryDTO.java
package org.mbc.czo.function.cart.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 장바구니 요약 정보를 전달하기 위한 DTO
 *
 * - API 응답(Response)이나 View 모델로 활용
 */
@Getter
@Setter
public class CartSumDTO { // 장바구니 화면에서 전체 상품 목록과 총 금액을 보여줄 때 사용

    /**
     * 장바구니에 담긴 상품 목록
     * - 각 항목은 CartItemDTO 형태
     */
    private List<CartItemDTO> cartItems;

    /**
     * 장바구니 전체 총액
     * - cartItems의 price * count 합계
     */
    private int totalPrice;
}
