package shoppingmall.ankim.domain.payment.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class AlreadyApprovedException extends CustomLogicException {
    public AlreadyApprovedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
