package org.mbc.czo.function.cart.repository;

import org.mbc.czo.function.cart.domain.Order;
import org.mbc.czo.function.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 주문(Order) 엔티티에 대한 JPA Repository
 *
 * - Spring Data JPA를 사용하여 CRUD 및 사용자 정의 쿼리 제공
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 특정 회원 이메일로 주문 목록 조회 (페이징 포함)
     *
     * @param email   회원 이메일
     * @param pageable 페이징 정보
     * @return 주문 리스트 (주문일 기준 내림차순)
     */
    @Query("select o from Order o " +
            "where o.member.memail = :email " +
            "order by o.orderDate desc"
    )
    List<Order> findOrders(@Param("email") String email, Pageable pageable);

    /**
     * 특정 회원 이메일로 주문 개수 조회
     *
     * @param email 회원 이메일
     * @return 주문 총 개수
     */
    @Query("select count(o) from Order o " +
            "where o.member.memail = :email"
    )
    Long countOrder(@Param("email") String email);

    /**
     * 특정 회원(Member)으로 주문 조회
     *
     * @param member 회원 엔티티
     * @return 해당 회원이 한 모든 주문 리스트
     */
    List<Order> findByMember(Member member);
}
