package shoppingmall.ankim.domain.delivery.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryStatus {
    PREPARING("배송 준비 중"),
    IN_PROGRESS("배송 중"),
    COMPLETED("배송 완료"),
    CANCELED("배송 취소"),
    RETURN_REQUESTED("반품 요청"),
    RETURN_COMPLETED("반품 완료");

    private final String text;

    // 주문 취소 가능 여부 확인
    public boolean canCancel() {
        return this == PREPARING;
    }
}
