package shoppingmall.ankim.domain.cart.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class CartItemNotSellingException extends CustomLogicException {
    public CartItemNotSellingException(ErrorCode errorCode) {
        super(errorCode);
    }
}
