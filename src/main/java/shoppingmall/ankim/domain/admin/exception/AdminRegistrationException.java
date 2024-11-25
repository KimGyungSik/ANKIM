package shoppingmall.ankim.domain.admin.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class AdminRegistrationException extends CustomLogicException {
    public AdminRegistrationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
