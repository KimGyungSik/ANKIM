package shoppingmall.ankim.domain.memberLeave.exception;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class MemberLeaveException extends CustomLogicException {
    public MemberLeaveException(ErrorCode errorCode) {
        super(errorCode);
    }
}
