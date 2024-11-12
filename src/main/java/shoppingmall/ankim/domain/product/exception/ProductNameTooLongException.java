package shoppingmall.ankim.domain.product.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class ProductNameTooLongException extends CustomLogicException {
    public ProductNameTooLongException(ErrorCode errorCode) {
        super(errorCode);
    }
}
