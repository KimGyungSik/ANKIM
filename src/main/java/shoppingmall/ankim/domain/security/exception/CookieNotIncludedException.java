package shoppingmall.ankim.domain.security.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class CookieNotIncludedException extends CustomLogicException {
    public CookieNotIncludedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
