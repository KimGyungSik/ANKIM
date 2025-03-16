package shoppingmall.ankim.domain.orderItem.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class FinalPriceException extends CustomLogicException {
    public FinalPriceException(ErrorCode errorCode) {
        super(errorCode);
    }
}
