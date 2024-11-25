package shoppingmall.ankim.domain.option.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class OptionValueNotFoundException extends CustomLogicException {
    public OptionValueNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
