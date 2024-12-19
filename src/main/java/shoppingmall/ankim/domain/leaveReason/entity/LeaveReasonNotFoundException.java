package shoppingmall.ankim.domain.leaveReason.entity;

import shoppingmall.ankim.global.exception.CustomLogicException;
import shoppingmall.ankim.global.exception.ErrorCode;

public class LeaveReasonNotFoundException extends CustomLogicException {
    public LeaveReasonNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
