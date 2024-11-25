package shoppingmall.ankim.domain.email.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class MailVerifyCodeException extends CustomLogicException {
    public MailVerifyCodeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
