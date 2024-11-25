package shoppingmall.ankim.domain.security.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class UserNotFoundException extends CustomLogicException {
    public UserNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
