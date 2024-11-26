package shoppingmall.ankim.domain.address.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class AddressNotFoundException extends CustomLogicException {
    public AddressNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
