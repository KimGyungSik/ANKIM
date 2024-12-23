package shoppingmall.ankim.domain.email.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class MailVerificationNotCompletedException extends CustomLogicException {
    public MailVerificationNotCompletedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
