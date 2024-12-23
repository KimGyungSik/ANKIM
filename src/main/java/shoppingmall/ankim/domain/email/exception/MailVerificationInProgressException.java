package shoppingmall.ankim.domain.email.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class MailVerificationInProgressException extends CustomLogicException {
    public MailVerificationInProgressException(ErrorCode errorCode) {
        super(errorCode);
    }
}
