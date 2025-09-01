package org.mbc.czo.function.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 장바구니에 담을 상품 정보를 담는 DTO
 *
 * - 사용자가 특정 상품을 장바구니에 추가할 때 요청(Request)으로 사용
 * - 장바구니 화면(View)이나 API 응답(Response)에서도 사용 가능
 */
@Getter
@Setter
public class CartItemDTO { // 상품을 장바구니에 추가할때 요청으로 사용되는 DTO

    /**
     * 상품 ID (필수 값)
     * - 어떤 상품을 장바구니에 담을지 지정하기 위해 필요
     */
    @NotNull(message = "상품 아이디는 필수 입력 값 입니다.")
    private Long itemId;

    /**
     * 담을 수량
     * - 최소 1개 이상 담아야 함
     */
    @Min(value = 1, message = "최소 1개 이상 담아주세요")
    private int count;

    /**
     * 상품명
     * - 뷰에서 상품 이름을 보여줄 때 사용
     */
    private String itemNm;

    /**
     * 상품 가격
     * - 장바구니 총액 계산 시 사용
     */
    private int price;

    private Long cartItemId;

    private int totalPrice;

}
