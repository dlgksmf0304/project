package org.mbc.czo.function.product.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.mbc.czo.function.product.constant.ItemSellStatus;

@ToString
@Getter
@Setter
public class ItemSearchDto {

    private String searchDateType;

    private ItemSellStatus searchSellStatus;

    private String searchBy;

    private String searchQuery = "";

    private String sortBy;      // 정렬 기준 (priceAsc, priceDesc)
    /**
     * 정렬 기준
     * priceAsc: 낮은 가격순
     * priceDesc: 높은 가격순
     * likesDesc: 좋아요 많은순
     * viewsAsc: 조회수 낮은순
     * viewsDesc: 조회수 높은순
     */

}
