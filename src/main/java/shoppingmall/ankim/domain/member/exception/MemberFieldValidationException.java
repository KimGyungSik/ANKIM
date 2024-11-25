package shoppingmall.ankim.domain.member.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class MemberFieldValidationException extends CustomLogicException {
    public MemberFieldValidationException(ErrorCode errorCode) {
        super(errorCode);
    }
}