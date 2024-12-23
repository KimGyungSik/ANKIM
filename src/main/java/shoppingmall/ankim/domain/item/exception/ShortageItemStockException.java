package shoppingmall.ankim.domain.item.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class ShortageItemStockException extends CustomLogicException {
    public ShortageItemStockException(ErrorCode errorCode) {
        super(errorCode);
    }
}
