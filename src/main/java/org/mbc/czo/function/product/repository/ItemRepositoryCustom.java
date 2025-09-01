package org.mbc.czo.function.product.repository;

import org.mbc.czo.function.product.domain.Item;
import org.mbc.czo.function.product.dto.ItemSearchDto;
import org.mbc.czo.function.product.dto.MainItemDto;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;

public interface ItemRepositoryCustom {

    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, org.springframework.data.domain.Pageable pageable);
    // 상품 조회 조건을 담고 있는 itemSearchDto 객체와 페이징 정보를 담고 있는 pageable 객체를 파라미터로 받는 getAdminItemPage 메소드를 정의. 반환데이터로 Page<Item>객체를 반환
    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, org.springframework.data.domain.Pageable pageable);
}
