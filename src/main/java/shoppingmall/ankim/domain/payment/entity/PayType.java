package shoppingmall.ankim.domain.payment.entity;

import lombok.Getter;

@Getter
public enum PayType {
    CARD("카드"),
    CASH("현금"),
    POINT("포인트");

    private final String description;

    PayType(String description) {
        this.description = description;
    }
}
