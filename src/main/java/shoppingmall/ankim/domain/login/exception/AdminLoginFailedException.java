package shoppingmall.ankim.domain.login.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class AdminLoginFailedException extends CustomLogicException {
    public AdminLoginFailedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
