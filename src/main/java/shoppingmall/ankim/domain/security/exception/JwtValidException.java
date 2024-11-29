package shoppingmall.ankim.domain.security.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class JwtValidException extends CustomLogicException {
    public JwtValidException(ErrorCode errorCode) {
        super(errorCode);
    }
}
