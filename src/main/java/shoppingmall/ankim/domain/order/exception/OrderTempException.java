package shoppingmall.ankim.domain.order.exception;

import lombok.Getter;
import shoppingmall.ankim.global.exception.ErrorCode;

@Getter
public class OrderTempException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String referer;

    public OrderTempException(ErrorCode errorCode, String referer) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.referer = referer;
    }
}