package shoppingmall.ankim.domain.option.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class OptionGroupNotFoundException extends CustomLogicException {
    public OptionGroupNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
