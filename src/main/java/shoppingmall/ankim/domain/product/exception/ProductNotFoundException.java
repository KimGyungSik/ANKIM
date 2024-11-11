package shoppingmall.ankim.domain.product.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class ProductNotFoundException extends CustomLogicException {
    public ProductNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
