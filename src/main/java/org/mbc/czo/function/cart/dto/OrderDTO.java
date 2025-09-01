package org.mbc.czo.function.cart.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 주문할 상품 정보를 전달하기 위한 DTO
 *
 * - 사용자가 특정 상품을 주문할 때 요청(Request)으로 사용
 * - 주문 수량과 상품 ID를 포함
 */
@Getter
@Setter
public class  OrderDTO { // 장바구니를 거치지 않고 바로 주문했을때 사용하는 DTO
    // ex) 상세페이지에서 '바로구매' 버튼을 눌렀을때 사용

    /**
     * 주문할 상품 ID (Item의 PK)
     * - 필수 입력 값
     */
    @NotNull(message = "상품 아이디는 필수 입력 값입니다.")
    private Long item_id;

    /**
     * 주문 수량
     * - 최소 1개 이상
     * - 최대 999개
     */
    @Min(value = 1, message = "최소 주문 수량은 1개입니다.")
    @Max(value = 999, message = "최대 주문 수량은 999개 입니다.")
    private int count;
}
