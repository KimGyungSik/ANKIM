package shoppingmall.ankim.domain.product.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class CannotModifySellingProductException extends CustomLogicException {
    public CannotModifySellingProductException(ErrorCode errorCode) {
        super(errorCode);
    }
}
