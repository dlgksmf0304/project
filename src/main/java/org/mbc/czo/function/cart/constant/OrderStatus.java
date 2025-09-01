package org.mbc.czo.function.cart.constant;

/**
 * 주문 상태를 나타내는 Enum
 *
 * ORDER  : 주문 완료 상태 (정상적으로 주문이 접수됨)
 * CANCEL : 주문 취소 상태 (사용자/관리자에 의해 주문이 취소됨)
 */
public enum OrderStatus {
    ORDER,  // 주문 완료
    CANCEL  // 주문 취소
}
