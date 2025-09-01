package org.mbc.czo.function.product.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.mbc.czo.function.product.constant.ItemSellStatus;
import org.mbc.czo.function.product.domain.Item;
import org.mbc.czo.function.product.domain.QItem;
import org.mbc.czo.function.product.domain.QItemImg;
import org.mbc.czo.function.product.dto.ItemSearchDto;
import org.mbc.czo.function.product.dto.MainItemDto;
import org.mbc.czo.function.product.dto.QMainItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ItemRepositoryImpl implements ItemRepositoryCustom { // ItemRepositoryCustom 상속

    private JPAQueryFactory queryFactory; // 동적으로 쿼리를 생성하기 위해 JPAQueryFactory 클래스 사용

    public ItemRepositoryImpl(EntityManager em) { // JPAQueryFactory의 생성자로 객체를 넣어 줌
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) { // 상품 판매 상태 조건이 전체 null 일 경우 null을 리턴
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    private BooleanExpression regDtsAfter(String searchDateType) { // searchDateType의 값에 따라서 dateTime의 값을 이전 시간의 값으로 세팅 후 해당 시간 이후로 등록된 상품만 조회
        LocalDateTime dateTime = LocalDateTime.now();

        if(StringUtils.equals("all", searchDateType) || searchDateType == null) {
            return null;
        } else if(StringUtils.equals("id", searchDateType)) {
            dateTime = dateTime.minusDays(1);
        } else if(StringUtils.equals("1w", searchDateType)) {
            dateTime = dateTime.minusWeeks(1);
        } else if(StringUtils.equals("1m", searchDateType)) {
            dateTime = dateTime.minusMonths(1);
        } else if(StringUtils.equals("6m", searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        }

        return QItem.item.regTime.after(dateTime);
    }

    private  BooleanExpression searchByLike(String searchBy, String searchQuery) { // 상품을 조회하도록 조건값을 반환

        if(StringUtils.equals("itemNm",searchBy)) {
            return QItem.item.itemNm.like("%" + searchQuery + "%"); // 앞뒤 어디에 있던 포함된 데이터 // %가 앞에 있으면 ~로 끝나는 데이터 뒤에 있으면 ~시작하는 데이터
        } else if(StringUtils.equals("createdBy",searchBy)) {
            return QItem.item.createdBy.like("%" + searchQuery + "%");
        }

        return null;
    }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QueryResults<Item> results = queryFactory // queryFactory를 이용해 쿼리 생성
                .selectFrom(QItem.item) // 상품데이터를 조회하기 위해서 Qitem의 item을 지정
                .where(regDtsAfter(itemSearchDto.getSearchDateType()), // 조건절 : BooleanExpression반환하는 조건문들을 넣어줌 ','단위로 넣어줄 경우 and 조건으로 인식
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(),
                                itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset()) // 데이터를 가지고 올 시작 인덱스를 지정
                .limit(pageable.getPageSize()) // 한번에 가지고 올 최대 개수를 지정
                .fetchResults(); // 조회한 리스트 및 전체 개수를 포함하는 QueryResults를 반환 상품 데이터 리스트 조회 및 상품 데이터 전체 개수를 조회하는 2번의 쿼리문이 실행

        List<Item> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total); // page 클래스의 구현체인 pageimpl 객체로 반환
    }

    private BooleanExpression itemNmLike(String searchQuery) { // 검새어가 널이 아니면  상품명에 해당 검색어가 포함되는 상품을 조회하는 조건을 반환
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%" + searchQuery + "%");
    }

    /*@Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        QueryResults<MainItemDto> results = queryFactory
                .select(
                        new QMainItemDto( // QMainitemDto의 생성자 반환 할 값들을 넣어준다. @QueryProjection사용하면 DTO로 바로 조회가 가능 하다 엔티티 조회 후 DTO로 변환하는 과정을 줄일 수 있다.
                        item.id,
                        item.itemNm,
                        item.itemDetail,
                        itemImg.imgUrl,
                        item.price)
                )
                .from(itemImg)
                .join(itemImg.item,item)
                .where(itemImg.repimgYn.eq("Y")) // 상품 이미지의 경우 대표 상품 이미지만 불러옴
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MainItemDto> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }*/

    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        QueryResults<MainItemDto> results = queryFactory
                .select(
                        new QMainItemDto(
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price
                        )
                )
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repimgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(toOrderSpecifiers(pageable.getSort(), item))   // 정렬 반영
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MainItemDto> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }
    private OrderSpecifier<?>[] toOrderSpecifiers(Sort sort, QItem item) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();
        for (Sort.Order o : sort) {
            PathBuilder<Item> pathBuilder = new PathBuilder<>(Item.class, "item");
            orders.add(new OrderSpecifier<>(
                    o.isAscending() ? Order.ASC : Order.DESC,
                    pathBuilder.getComparable(o.getProperty(), Comparable.class)
            ));
        }
        return orders.toArray(new OrderSpecifier<?>[0]);
    }

}