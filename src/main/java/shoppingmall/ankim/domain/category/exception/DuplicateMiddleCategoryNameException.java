package shoppingmall.ankim.domain.category.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class DuplicateMiddleCategoryNameException extends CustomLogicException {
    public DuplicateMiddleCategoryNameException(ErrorCode errorCode) {
        super(errorCode);
    }
}
