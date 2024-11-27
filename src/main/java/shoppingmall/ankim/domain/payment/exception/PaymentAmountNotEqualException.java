package shoppingmall.ankim.domain.payment.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class PaymentAmountNotEqualException extends CustomLogicException {
    public PaymentAmountNotEqualException(ErrorCode errorCode) {
        super(errorCode);
    }
}
