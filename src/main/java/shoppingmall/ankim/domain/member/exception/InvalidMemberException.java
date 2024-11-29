package shoppingmall.ankim.domain.member.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class InvalidMemberException extends CustomLogicException {
    public InvalidMemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
