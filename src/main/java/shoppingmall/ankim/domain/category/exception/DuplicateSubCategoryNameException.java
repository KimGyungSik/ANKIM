package shoppingmall.ankim.domain.category.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class DuplicateSubCategoryNameException extends CustomLogicException {
    public DuplicateSubCategoryNameException(ErrorCode errorCode) {
        super(errorCode);
    }
}
