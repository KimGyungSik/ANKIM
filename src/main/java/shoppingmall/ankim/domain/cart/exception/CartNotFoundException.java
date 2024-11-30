package shoppingmall.ankim.domain.cart.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class CartNotFoundException extends CustomLogicException {
    public CartNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
