package shoppingmall.ankim.domain.category.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class CategoryNameTooLongException extends CustomLogicException {
    public CategoryNameTooLongException(ErrorCode errorCode) {
        super(errorCode);
    }
}
