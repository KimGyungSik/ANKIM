package shoppingmall.ankim.domain.category.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class CategoryLinkedWithProductException extends CustomLogicException {
    public CategoryLinkedWithProductException(ErrorCode errorCode) {
        super(errorCode);
    }
}
