package shoppingmall.ankim.domain.orderItem.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    INIT("주문생성"),
    COMPLETED("주문완료"),
    CANCELED("주문취소"),
    EXCHANGE_REQUESTED("교환신청"),
    EXCHANGE_COMPLETED("교환완료");

    private final String text;
}
