package org.mbc.czo.function.cart.dto;

import lombok.Getter;
import lombok.Setter;
import org.mbc.czo.function.cart.domain.OrderItem;

@Getter
@Setter
public class OrderItemDTO { // 사용자가 결제 완료 후 주문내역을 조회할 때 사용
    // 주문(OrderItem)중심 DTO

    // 주문 상품명
    private String itemNm;

    // 주문수량
    private int count;

    // 주문 당시 상품 가격
    private int orderPrice;


    // 상품이미지 url
    private String imgUrl;

    /**
     * 생성자 - OrderItem 엔티티와 이미지 URL을 받아 DTO 필드 초기화
     *
     * @param orderItem 주문상품 엔티티
     * @param imgUrl    상품 이미지 URL
     */
    public OrderItemDTO(OrderItem orderItem, String imgUrl) {
        this.itemNm = orderItem.getItem().getItemNm(); // 상품명
        this.count = orderItem.getCount();            // 주문 수량
        this.orderPrice = orderItem.getOrderPrice();  // 주문 당시 가격
        this.imgUrl = imgUrl;                          // 이미지 URL
    }
}
