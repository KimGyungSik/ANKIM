package shoppingmall.ankim.domain.item.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class InvalidQuantityException extends CustomLogicException {
    public InvalidQuantityException(ErrorCode errorCode) {
        super(errorCode);
    }
}
