package shoppingmall.ankim.domain.payment.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class InvalidPaymentKeyException extends CustomLogicException {
    public InvalidPaymentKeyException(ErrorCode errorCode) {
        super(errorCode);
    }
}
