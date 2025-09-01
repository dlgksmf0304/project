package org.mbc.czo.function.product.repository;

import org.mbc.czo.function.product.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item>, ItemRepositoryCustom {
// Repository를 인터페이스로 만들지 않으면, Spring Data JPA가 구현체를 자동 생성하지 못해서 서비스 주입 시 Bean이 없어 오류가 발생


    // ▼ [추가] 조회수 증가 메소드
    @Transactional
    @Modifying
    @Query("update Item i set i.views = i.views + 1 where i.id = :id")
    void updateView(@Param("id") Long id);

    // ▼ [추가] 좋아요 수 증가 메소드
    @Transactional
    @Modifying
    @Query("update Item i set i.likes = i.likes + 1 where i.id = :id")
    void addLike(@Param("id") Long id);
}
