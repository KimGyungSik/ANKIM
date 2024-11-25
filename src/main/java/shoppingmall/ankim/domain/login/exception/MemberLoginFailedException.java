package shoppingmall.ankim.domain.login.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class MemberLoginFailedException extends CustomLogicException {
    public MemberLoginFailedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
