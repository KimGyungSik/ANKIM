package shoppingmall.ankim.domain.category.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class CategoryNotFoundException extends CustomLogicException {
    public CategoryNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
