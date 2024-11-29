package shoppingmall.ankim.domain.cart.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class UnauthorizedCartItemAccessException extends CustomLogicException {
    public UnauthorizedCartItemAccessException(ErrorCode errorCode) {
        super(errorCode);
    }
}
