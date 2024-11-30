package shoppingmall.ankim.domain.cart.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class CartItemNotFoundException extends CustomLogicException {
    public CartItemNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
