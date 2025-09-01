package org.mbc.czo.function.cart.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.mbc.czo.function.common.entity.BaseEntity;
import org.mbc.czo.function.product.domain.Item;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class OrderItem extends BaseEntity {
    // 주문한 상품 단위(장바구니에 들어간 여러 상품들 중 하나)

    @Id
    @GeneratedValue
    @Column(name = "order_item_id") // PK 컬럼명 지정
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    // 주문상품(OrderItem) ↔ 상품(Item) 관계 (다대일)
    // 여러 OrderItem이 하나의 Item을 참조할 수 있음
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    // 주문상품(OrderItem) ↔ 주문(Order) 관계 (다대일)
    // 여러 OrderItem이 하나의 Order에 속할 수 있음
    private Order order;

    // 주문 당시 상품 가격 (Item.price 값을 복사)
    private int orderPrice;

    // 주문 수량
    private int count;

    // 등록/수정 시간 (BaseEntity와 중복 가능 → 필요 시 제거 가능)
    private LocalDateTime regTime;
    private LocalDateTime updateTime;

    // === 생성 메서드 ===
    // 주문상품 생성 시 사용
    public static OrderItem createOrderItem(Item item, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);           // 주문 상품 지정
        orderItem.setCount(count);         // 주문 수량 지정
        orderItem.setOrderPrice(item.getPrice()); // 주문 당시 상품 가격 저장

        item.removeStock(count); // 주문 시 상품 재고 감소 처리
        return orderItem;
    }

    // === 비즈니스 로직 ===
    // 주문상품 총 가격 (상품 가격 × 수량)
    public int getTotalPrice() {
        return orderPrice * count;
    }

    /**
     * 주문 취소 시 재고 복구
     */
    public void cancel() {
        item.addStock(count); // Item 엔티티에 addStock(int count) 필요
    }

}
