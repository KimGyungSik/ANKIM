package shoppingmall.ankim.domain.payment.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class InvalidOrderStatusException extends CustomLogicException {
    public InvalidOrderStatusException(ErrorCode errorCode) {
        super(errorCode);
    }
}
