package shoppingmall.ankim.domain.item.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class ItemNotFoundException extends CustomLogicException {
    public ItemNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
