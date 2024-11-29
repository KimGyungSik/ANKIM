package shoppingmall.ankim.domain.payment.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class PaymentNotFoundException extends CustomLogicException {
    public PaymentNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
