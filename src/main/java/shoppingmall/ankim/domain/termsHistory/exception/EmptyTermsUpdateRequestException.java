package shoppingmall.ankim.domain.termsHistory.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class EmptyTermsUpdateRequestException extends CustomLogicException {
    public EmptyTermsUpdateRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
