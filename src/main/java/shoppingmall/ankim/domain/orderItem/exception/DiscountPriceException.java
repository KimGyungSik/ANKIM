package shoppingmall.ankim.domain.orderItem.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class DiscountPriceException extends CustomLogicException {
    public DiscountPriceException(ErrorCode errorCode) {
        super(errorCode);
    }
}
