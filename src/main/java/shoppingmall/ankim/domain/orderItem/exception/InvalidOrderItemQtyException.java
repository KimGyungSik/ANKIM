package shoppingmall.ankim.domain.orderItem.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class InvalidOrderItemQtyException extends CustomLogicException {
    public InvalidOrderItemQtyException(ErrorCode errorCode) {
        super(errorCode);
    }
}
