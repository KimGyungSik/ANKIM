package shoppingmall.ankim.domain.terms.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class TermsNotFoundException extends CustomLogicException {
    public TermsNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
