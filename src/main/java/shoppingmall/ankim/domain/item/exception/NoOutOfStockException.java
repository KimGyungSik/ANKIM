package shoppingmall.ankim.domain.item.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class NoOutOfStockException extends CustomLogicException {
    public NoOutOfStockException(ErrorCode errorCode) {
        super(errorCode);
    }
}
