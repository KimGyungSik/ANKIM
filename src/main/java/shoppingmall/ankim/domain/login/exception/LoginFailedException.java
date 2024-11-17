package shoppingmall.ankim.domain.login.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class LoginFailedException extends CustomLogicException {
    public LoginFailedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
