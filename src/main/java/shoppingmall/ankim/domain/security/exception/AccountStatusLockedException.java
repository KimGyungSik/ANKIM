package shoppingmall.ankim.domain.security.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class AccountStatusLockedException extends CustomLogicException {
    public AccountStatusLockedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
