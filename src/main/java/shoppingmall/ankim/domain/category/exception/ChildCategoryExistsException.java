package shoppingmall.ankim.domain.category.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class ChildCategoryExistsException extends CustomLogicException {
    public ChildCategoryExistsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
