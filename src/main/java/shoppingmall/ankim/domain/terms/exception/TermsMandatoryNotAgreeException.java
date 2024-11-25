package shoppingmall.ankim.domain.terms.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class TermsMandatoryNotAgreeException extends CustomLogicException {
    public TermsMandatoryNotAgreeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
