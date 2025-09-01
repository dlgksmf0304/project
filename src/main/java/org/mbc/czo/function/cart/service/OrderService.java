package org.mbc.czo.function.cart.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.mbc.czo.function.cart.domain.Cart;
import org.mbc.czo.function.cart.domain.CartItem;
import org.mbc.czo.function.cart.domain.Order;
import org.mbc.czo.function.cart.domain.OrderItem;
import org.mbc.czo.function.cart.dto.OrderDTO;
import org.mbc.czo.function.cart.repository.CartItemRepository;
import org.mbc.czo.function.cart.repository.CartRepository;
import org.mbc.czo.function.cart.repository.OrderRepository;
import org.mbc.czo.function.member.domain.Member;
import org.mbc.czo.function.member.repository.MemberJpaRepository;
import org.mbc.czo.function.product.domain.Item;
import org.mbc.czo.function.product.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    /**
     * 단일 상품 주문 (상세페이지 → 바로구매)
     */
    public Long order(OrderDTO orderDTO, String memail) {
        // 상품 조회
        Item item = itemRepository.findById(orderDTO.getItem_id())
                .orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));

        // 회원 조회
        Member member = memberJpaRepository.findByMemail(memail)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다: " + memail));

        // 주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDTO.getCount());
        List<OrderItem> orderItemList = new ArrayList<>();
        orderItemList.add(orderItem);

        // 주문 생성 & 저장
        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        return order.getId();
    }

    /**
     * 다중 상품 주문 (여러 개 상품 바로 주문)
     */
    public Long orders(List<OrderDTO> orderDTOList, String memail) {
        // 회원 조회
        Member member = memberJpaRepository.findByMemail(memail)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다: " + memail));

        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderDTO orderDTO : orderDTOList) {
            Item item = itemRepository.findById(orderDTO.getItem_id())
                    .orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));
            OrderItem orderItem = OrderItem.createOrderItem(item, orderDTO.getCount());
            orderItemList.add(orderItem);
        }

        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        return order.getId();
    }

    /**
     * 장바구니 전체 주문
     * - 주문 완료 후 장바구니 비움
     */
    public Long orderFromCart(String memail) {
        // 회원 조회
        Member member = memberJpaRepository.findByMemail(memail)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다: " + memail));

        // 장바구니 조회
        Cart cart = cartRepository.findByMemberMemail(memail)
                .orElseThrow(() -> new EntityNotFoundException("장바구니가 존재하지 않습니다."));

        List<OrderItem> orderItemList = new ArrayList<>();

        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = OrderItem.createOrderItem(
                    cartItem.getItem(),
                    cartItem.getCount()
            );
            orderItemList.add(orderItem);
        }

        // 주문 생성 & 저장
        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        // 장바구니 비우기 (연관관계 제거 + DB 삭제)
        for (CartItem cartItem : new ArrayList<>(cart.getCartItems())) {
            cart.getCartItems().remove(cartItem);   // 연관관계 끊기
            cartItemRepository.delete(cartItem);    // DB에서 삭제
        }

        return order.getId();
    }

    @Transactional
    public void cancelOrder(Long orderId, String email) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        // 권한 확인: 로그인한 사용자만 자신의 주문 취소 가능
        if (!order.getMember().getMemail().equals(email)) {
            throw new IllegalStateException("주문 취소 권한이 없습니다.");
        }

        order.cancel(); // 상태 변경 + 재고 복구
    }


}
