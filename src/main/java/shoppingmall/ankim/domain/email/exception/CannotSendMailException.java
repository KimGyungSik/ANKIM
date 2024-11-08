package shoppingmall.ankim.domain.email.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class CannotSendMailException extends CustomLogicException {
    public CannotSendMailException(ErrorCode errorCode) {
        super(errorCode);
    }
}
