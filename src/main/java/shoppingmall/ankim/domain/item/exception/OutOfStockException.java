package shoppingmall.ankim.domain.item.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class OutOfStockException extends CustomLogicException {
    public OutOfStockException(ErrorCode errorCode) {
        super(errorCode);
    }
}
