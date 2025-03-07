package shoppingmall.ankim.domain.product.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class ProductNotSellingException extends CustomLogicException {
    public ProductNotSellingException(ErrorCode errorCode) {
        super(errorCode);
    }
}
