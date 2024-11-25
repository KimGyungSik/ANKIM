package shoppingmall.ankim.domain.security.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class JwtTokenException extends CustomLogicException {
    public JwtTokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
