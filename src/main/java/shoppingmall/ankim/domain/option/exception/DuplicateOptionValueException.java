package shoppingmall.ankim.domain.option.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class DuplicateOptionValueException extends CustomLogicException {
    public DuplicateOptionValueException(ErrorCode errorCode) {
        super(errorCode);
    }
}
