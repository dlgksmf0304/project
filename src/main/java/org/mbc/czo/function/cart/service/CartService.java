package org.mbc.czo.function.cart.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mbc.czo.function.cart.domain.Cart;
import org.mbc.czo.function.cart.domain.CartItem;
import org.mbc.czo.function.cart.domain.Order;
import org.mbc.czo.function.cart.dto.*;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    // =======================================================
    // 1. 장바구니에 상품 추가
    // =======================================================
    public Long addCart(CartItemDTO cartItemDTO, Member member) {
        Item item = itemRepository.findById(cartItemDTO.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));

        Cart cart = cartRepository.findByMemberMemail(member.getMemail())
                .orElseGet(() -> cartRepository.save(Cart.createCart(member)));

        CartItem savedCartItem = cartItemRepository.findByCart_IdAndItem_Id(cart.getId(), item.getId())
                .orElse(null);

        if (savedCartItem != null) {
            savedCartItem.addCount(cartItemDTO.getCount());
            return savedCartItem.getId();
        } else {
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDTO.getCount());
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }
    }

    // =======================================================
    // 2. 장바구니 조회
    // =======================================================
    @Transactional(readOnly = true)
    public List<CartItemDTO> getCartItems(String memail) {
        Member member = memberJpaRepository.findByMemail(memail)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다: " + memail));

        Cart cart = cartRepository.findByMemberMemail(member.getMemail())
                .orElseThrow(() -> new EntityNotFoundException("장바구니가 존재하지 않습니다."));

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            return Collections.emptyList();
        }

        return cart.getCartItems().stream()
                .map(cartItem -> {
                    CartItemDTO dto = new CartItemDTO();
                    dto.setCartItemId(cartItem.getId());
                    dto.setItemId(cartItem.getItem().getId());
                    dto.setItemNm(cartItem.getItem().getItemNm());
                    dto.setCount(cartItem.getCount());
                    dto.setPrice(cartItem.getItem().getPrice());
                    // 총 금액
                    dto.setTotalPrice(cartItem.getItem().getPrice() * cartItem.getCount());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // =======================================================
    // 3. 주문 내역 조회
    // =======================================================
    @Transactional(readOnly = true)
    public List<OrderHistDTO> getOrderHist(String memail) {
        Member member = memberJpaRepository.findByMemail(memail)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다: " + memail));

        List<Order> orders = orderRepository.findByMember(member);

        return orders.stream()
                .map(order -> {
                    OrderHistDTO dto = new OrderHistDTO();
                    dto.setOrderId(order.getId());
                    dto.setOrderDate(order.getOrderDate());
                    dto.setStatus(order.getOrderStatus().toString());

                    List<OrderHistDTO.OrderItemDTO> orderItemDTOs = order.getOrderItems().stream()
                            .map(orderItem -> {
                                OrderHistDTO.OrderItemDTO itemDTO = new OrderHistDTO.OrderItemDTO();
                                itemDTO.setItemNm(orderItem.getItem().getItemNm());
                                itemDTO.setCount(orderItem.getCount());
                                itemDTO.setOrderPrice(orderItem.getOrderPrice());
                                return itemDTO;
                            })
                            .collect(Collectors.toList());

                    dto.setOrderItems(orderItemDTOs);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // =======================================================
    // 4. 장바구니 아이템 수량 수정
    // =======================================================
    @Transactional
    public void updateCartItemCount(Long cartItemId, int count, String email) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니 아이템이 존재하지 않습니다."));

        if (!cartItem.getCart().getMember().getMemail().equals(email)) {
            throw new IllegalArgumentException("다른 회원의 장바구니는 수정할 수 없습니다.");
        }

        cartItem.updateCount(count);
    }

    // =======================================================
    // 5. 장바구니 아이템 소유자 검증
    // =======================================================
    @Transactional(readOnly = true)
    public boolean isCartItemOwnedByUser(Long cartItemId, String email) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니 아이템이 존재하지 않습니다."));

        return cartItem.getCart().getMember().getMemail().equals(email);
    }

    // =======================================================
    // 6. 장바구니 항목 삭제
    // =======================================================
    public void removeCartItem(Long cartItemId, String memail) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니 항목이 존재하지 않습니다: " + cartItemId));

        if (!cartItem.getCart().getMember().getMemail().equals(memail)) {
            throw new IllegalStateException("본인 장바구니가 아닙니다.");
        }

        cartItem.getCart().getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
    }

    // =======================================================
    // 7. 장바구니 전체 비우기
    // =======================================================
    public void clearCart(String memail) {
        Cart cart = cartRepository.findByMemberMemail(memail)
                .orElseThrow(() -> new EntityNotFoundException("장바구니가 없습니다."));

        if (!cart.getMember().getMemail().equals(memail)) {
            throw new IllegalStateException("본인 장바구니가 아닙니다.");
        }

        cart.getCartItems().clear();
    }

    // =======================================================
// 8. 장바구니 아이템 주문
// =======================================================
    public Long orderCartItem(List<CartOrderDTO> cartOrderDTOList, String memail) {
        List<OrderDTO> orderDTOList = new ArrayList<>();

        for (CartOrderDTO cartOrderDTO : cartOrderDTOList) {
            CartItem cartItem = cartItemRepository
                    .findById(cartOrderDTO.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);

            // cartItem 기준으로 최종 수량과 가격을 맞춘다 (신뢰성 확보)
            int finalCount = cartItem.getCount();
            int price = cartItem.getItem().getPrice();

            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setItem_id(cartItem.getItem().getId());
            orderDTO.setCount(finalCount);  // 실제 장바구니 수량 반영

            orderDTOList.add(orderDTO);

            // CartOrderDTO 에도 값 맞춰주기
            cartOrderDTO.setCount(finalCount);
            cartOrderDTO.setPrice(price);
            cartOrderDTO.setTotalPrice(price * finalCount);
        }

        // 주문 생성
        Long orderId = orderService.orders(orderDTOList, memail);

        // ✅ 주문 완료 후 선택된 장바구니 아이템 삭제
        for (CartOrderDTO cartOrderDTO : cartOrderDTOList) {
            CartItem cartItem = cartItemRepository
                    .findById(cartOrderDTO.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);

            Cart cart = cartItem.getCart();           // Cart 가져오기
            cart.getCartItems().remove(cartItem);     // ✅ 연관관계 제거 (orphanRemoval 발동)
            cartItemRepository.delete(cartItem);      // ✅ DB에서도 제거
        }

        return orderId;
    }

    // =======================================================
    // 9. 장바구니 총액 조회
    // =======================================================
    @Transactional(readOnly = true)
    public CartSumDTO getCartSum(String memail) {
        List<CartItemDTO> cartItems = getCartItems(memail);

        int total = cartItems.stream()
                .mapToInt(CartItemDTO::getTotalPrice)
                .sum();

        CartSumDTO summary = new CartSumDTO();
        summary.setCartItems(cartItems);
        summary.setTotalPrice(total);
        return summary;
    }

    // =======================================================
    // 10. 주문 화면에 장바구니 정보 그대로 넘기기
    // =======================================================
    @Transactional(readOnly = true)
    public List<CartOrderDTO> getCartOrdersForUser(String email) {
        List<CartItemDTO> cartItems = getCartItems(email);
        List<CartOrderDTO> cartOrderDTOList = new ArrayList<>();

        for (CartItemDTO cartItem : cartItems) {
            CartOrderDTO dto = new CartOrderDTO();
            dto.setCartItemId(cartItem.getCartItemId());
            dto.setItemNm(cartItem.getItemNm());
            dto.setCount(cartItem.getCount());
            dto.setPrice(cartItem.getPrice());
            dto.setTotalPrice(cartItem.getTotalPrice());
            cartOrderDTOList.add(dto);
        }

        return cartOrderDTOList;
    }
}
