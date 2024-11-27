package shoppingmall.ankim.domain.order.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class OrderNotFoundException extends CustomLogicException {
    public OrderNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
