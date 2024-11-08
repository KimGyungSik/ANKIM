package shoppingmall.ankim.domain.member.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class MemberRegistrationException extends CustomLogicException {
    public MemberRegistrationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
