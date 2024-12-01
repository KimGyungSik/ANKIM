package shoppingmall.ankim.domain.address.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class AddressRegisterException extends CustomLogicException {
    public AddressRegisterException(ErrorCode errorCode) {
        super(errorCode);
    }
}