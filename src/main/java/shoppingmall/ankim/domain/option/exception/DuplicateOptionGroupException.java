package shoppingmall.ankim.domain.option.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class DuplicateOptionGroupException extends CustomLogicException {
    public DuplicateOptionGroupException(ErrorCode errorCode) {
        super(errorCode);
    }
}
