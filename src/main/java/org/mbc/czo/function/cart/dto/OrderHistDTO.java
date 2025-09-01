package org.mbc.czo.function.cart.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 주문 내역을 전달하기 위한 DTO
 *
 * - 장바구니 또는 주문 내역 화면에서 사용자 주문 정보를 보여줄 때 사용
 * - 주문 ID, 주문일, 주문 상태, 주문 상품 리스트 포함
 */
@Getter @Setter
public class OrderHistDTO { // 주문내역 화면을 불러올 때 사용하는 DTO

    // 주문 ID (Order PK)
    private Long orderId;

    // 주문 날짜 및 시간
    private LocalDateTime orderDate;

    /**
     * 주문 상태
     * - 예: ORDER, CANCEL
     */
    private String status;

    /**
     * 주문 상품 리스트
     * - 각 상품은 OrderItemDTO 형태
     */
    private List<OrderItemDTO> orderItems;

    private String memberId;

    // 주문 상품정보를 담는 내부 DTO
    @Getter @Setter
    public static class OrderItemDTO {

        // 상품명
        private String itemNm;

        // 주문 수량
        private int count;

        // 주문 당시 상품 가격
        private int orderPrice;
    }
}
