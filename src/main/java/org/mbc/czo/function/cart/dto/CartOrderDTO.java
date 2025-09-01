package org.mbc.czo.function.cart.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 장바구니에서 주문할 상품 정보를 전달하기 위한 DTO
 *
 * - 단일 주문 또는 다중 주문(리스트) 모두 처리 가능
 */
@Getter
@Setter
public class CartOrderDTO { // 장바구니 화면에서 선택한 상품을 주문할 때 사용

    /**
     * 주문할 장바구니 항목 ID (CartItem의 PK)
     */
    private Long cartItemId;

    /**
     * 다중 주문 처리를 위한 리스트
     * - 한 번의 요청으로 여러 장바구니 항목을 주문할 수 있음
     */
    private List<CartOrderDTO> cartOrderDTOList;

    /**
     * 상품 단가
     */
    private int price;

    /**
     * 주문 수량
     */
    private int count;

    /**
     * 상품명
     */
    private String itemNm;

    /**
     * 총 금액 (price × count)
     */
    private int totalPrice;
}
