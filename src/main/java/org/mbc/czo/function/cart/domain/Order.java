package org.mbc.czo.function.cart.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.mbc.czo.function.cart.constant.OrderStatus;
import org.mbc.czo.function.common.entity.BaseEntity;
import org.mbc.czo.function.member.domain.Member;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends BaseEntity {
    // 회원이 결제를 완료했을 때 생성되는 거래 단위
    // 하나의 order는 여러개의 orderItem을 가질 수 있음
    // ex) 장바구니에 담긴 여러개의 상품

    @Id
    @GeneratedValue
    @Column(name = "order_id") // PK 컬럼명 지정
    private Long id; // 상품을 결제하고 주문했을때 생성되는 주문번호

    @ManyToOne(fetch = FetchType.LAZY)
    // 다대일 관계: 한 명의 회원(Member)이 여러 주문(Order)을 가질 수 있음
    @JoinColumn(name = "member_id")
    // 외래키(FK): member_id
    private Member member;

    // 주문일자
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    // Enum을 문자열 형태로 저장 (예: "ORDER", "CANCEL")
    private OrderStatus orderStatus;

    @OneToMany(
            mappedBy = "order",              // OrderItem의 order 필드와 매핑
            cascade = CascadeType.ALL,       // Order 저장/삭제 시 OrderItem도 함께 처리
            orphanRemoval = true,            // 고아 객체 제거 (리스트에서 빠지면 DB에서도 삭제)
            fetch = FetchType.LAZY           // 필요할 때만 조회
    )
    private List<OrderItem> orderItems = new ArrayList<>();


    // === 연관관계 편의 메서드 ===
    // Order ↔ OrderItem 양방향 관계를 묶어주는 메서드
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this); // OrderItem에도 Order 설정
    }

    // === 주문 생성 메서드 ===
    // 주문 객체를 생성하고 기본 상태값 설정
    public static Order createOrder(Member member, List<OrderItem> orderItemList){
        Order order = new Order();
        order.setMember(member);
        for(OrderItem orderItem : orderItemList){
            order.addOrderItem(orderItem); // 주문상품 추가
        }
        order.setOrderStatus(OrderStatus.ORDER); // 주문 상태: ORDER
        order.setOrderDate(LocalDateTime.now()); // 주문일자: 현재 시간
        return order;
    }

    // === 비즈니스 로직 ===
    // 주문 전체 금액 계산 (OrderItem들의 합계)
    public int getTotalPrice(){
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    // Order 엔티티 내부에 추가
    /**
     * 주문 취소
     * - 주문 상태를 CANCEL로 변경
     * - 주문에 포함된 각 OrderItem의 재고를 원래대로 복구
     */
    public void cancel() {
        if (this.orderStatus == OrderStatus.CANCEL) {
            throw new IllegalStateException("이미 취소된 주문입니다.");
        }
        this.orderStatus = OrderStatus.CANCEL;

        for (OrderItem item : orderItems) {
            item.cancel(); // OrderItem에 재고 복구 메서드 필요
        }
    }

}
