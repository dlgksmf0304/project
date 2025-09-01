package org.mbc.czo.function.cart.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.mbc.czo.function.common.entity.BaseEntity;
import org.mbc.czo.function.product.domain.Item;

@Entity // JPA 엔티티 선언
@Getter
@Setter
public class CartItem extends BaseEntity { // 공통 엔티티(BaseEntity: 생성일, 수정일 등) 상속
    // 장바구니에 담긴 상품 엔티티

    @Id // 기본키 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    // Cart : CartItem = 1 : N 관계 → 여러 CartItem이 하나의 Cart에 속함
    @JoinColumn(name = "cart_id")
    // 외래키(FK) 컬럼: cart_id - 장바구니와 연결
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    // Item : CartItem = 1 : N 관계 → 하나의 상품(Item)이 여러 CartItem에 담길 수 있음
    @JoinColumn(name = "item_id")
    // 외래키(FK) 컬럼: item_id - 아이템상품과 연결
    private Item item;

    // 담긴 수량
    private int count;

    // === 생성 메서드 ===
    // 장바구니 항목 생성 시 사용 (Cart, Item, 수량을 입력받아 CartItem 객체 생성)
    public static CartItem createCartItem(Cart cart, Item item, int count) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(count);
        return cartItem;
    }

    // === 비즈니스 로직 ===
    // 이미 장바구니에 담긴 상품일 경우 수량만 증가시킴
    public void addCount(int count) {
        this.count += count;
    }

    // 장바구니 수량을 특정 값으로 변경
    public void updateCount(int count) {
        this.count = count;
    }
}
