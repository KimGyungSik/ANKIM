package shoppingmall.ankim.domain.orderItem.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    PENDING_PAYMENT("결제 대기"),
    PAID("결제 완료"),
    CANCELED("결제 취소"),
    FAILED_PAYMENT("결제 실패");

    private final String text;
}
