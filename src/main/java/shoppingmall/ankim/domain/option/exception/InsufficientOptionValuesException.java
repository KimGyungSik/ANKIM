package shoppingmall.ankim.domain.option.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class InsufficientOptionValuesException extends CustomLogicException {
    public InsufficientOptionValuesException(ErrorCode errorCode) {
        super(errorCode);
    }
}
