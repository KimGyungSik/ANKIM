package shoppingmall.ankim.domain.cart.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class CartItemLimitExceededException extends CustomLogicException {
    public CartItemLimitExceededException(ErrorCode errorCode) {
        super(errorCode);
    }
}
