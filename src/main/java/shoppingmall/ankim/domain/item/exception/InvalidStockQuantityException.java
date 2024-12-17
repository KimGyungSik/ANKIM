package shoppingmall.ankim.domain.item.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class InvalidStockQuantityException extends CustomLogicException {
    public InvalidStockQuantityException(ErrorCode errorCode) {
        super(errorCode);
    }
}
