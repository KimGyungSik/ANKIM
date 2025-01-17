package shoppingmall.ankim.domain.security.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class JwtTokenException extends CustomLogicException {
    private final ErrorCode errorCode;

    public JwtTokenException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public int getStatusCode() {
        return errorCode.getHttpStatus().value();
    }
}
