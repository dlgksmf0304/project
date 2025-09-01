package org.mbc.czo.cart;

import org.junit.jupiter.api.Test;
import org.mbc.czo.function.cart.domain.CartItem;
import org.mbc.czo.function.cart.dto.CartItemDTO;
import org.mbc.czo.function.cart.service.CartService;
import org.mbc.czo.function.member.domain.Member;
import org.mbc.czo.function.member.repository.MemberJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
/*@Transactional*/
public class CartControllerTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private MemberJpaRepository memberRepository;

    @Test
    public void testAddItemAsGuestWithExistingItem() {
        // 1. 임시 회원 생성 또는 조회
        Member guest = memberRepository.findByMemail("guest@example.com")
                .orElseGet(() -> {
                    Member newGuest = Member.builder()
                            .mid("guest123")
                            .mname("임시회원")
                            .mphoneNumber("01000000000")
                            .memail("guest@example.com")
                            .mpassword("guest") // 테스트용
                            .mmileage(0L)
                            .misSocialActivate(false)
                            .build();
                    return memberRepository.save(newGuest);
                });

// 2. 장바구니 담기 위한 itemId와 count 지정
        Long itemId = 2452L; // DB에 있는 실제 상품 번호
        int count = 3;    // 담을 수량

        CartItemDTO dto = new CartItemDTO();
        dto.setItemId(itemId);
        dto.setCount(count);

// 3. 장바구니에 상품 담기 (String memail 전달)
        Long cartItemId = cartService.addCart(dto, guest);



    }
}
