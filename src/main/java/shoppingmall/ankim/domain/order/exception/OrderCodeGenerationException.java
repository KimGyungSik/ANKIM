package shoppingmall.ankim.domain.order.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class OrderCodeGenerationException extends CustomLogicException {
    public OrderCodeGenerationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
