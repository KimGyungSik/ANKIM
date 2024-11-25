package shoppingmall.ankim.domain.email.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class MailSendException extends CustomLogicException {
    public MailSendException(ErrorCode errorCode) {
        super(errorCode);
    }
}
