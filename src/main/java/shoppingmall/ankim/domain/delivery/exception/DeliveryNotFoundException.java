package shoppingmall.ankim.domain.delivery.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class DeliveryNotFoundException extends CustomLogicException {
    public DeliveryNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
