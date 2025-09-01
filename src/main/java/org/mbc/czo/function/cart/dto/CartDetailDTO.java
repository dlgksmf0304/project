package org.mbc.czo.function.cart.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 장바구니 상세 정보를 전달하기 위한 DTO
 *
 * 화면(View)이나 API 응답에서 장바구니에 담긴 상품의
 * 주요 정보(상품명, 가격, 수량, 이미지)를 보여줄 때 사용
 */
@Getter
@Setter
public class CartDetailDTO { // 장바구니에 담아둔 상품들을 조회할 때 사용
    // 장바구니(CartItem) 중심 DTO

    // 장바구니 아이템 ID (CartItem의 PK)
    private Long cartItemId;

    // 상품명
    private String ItemNm;

    // 상품 가격
    private int price;

    // 담은 수량
    private int count;

    // 상품 대표 이미지 URL
    private String imgUrl;

    /**
     * 생성자 - 장바구니 상세정보를 초기화할 때 사용
     *
     * @param cartItemId 장바구니 항목 ID
     * @param ItemNm     상품명
     * @param price      상품 가격
     * @param count      수량
     * @param imgUrl     이미지 URL
     */
    public CartDetailDTO(Long cartItemId, String ItemNm, int price, int count, String imgUrl) {
        this.cartItemId = cartItemId;
        this.ItemNm = ItemNm;
        this.price = price;
        this.count = count;
        this.imgUrl = imgUrl;
    }
}
