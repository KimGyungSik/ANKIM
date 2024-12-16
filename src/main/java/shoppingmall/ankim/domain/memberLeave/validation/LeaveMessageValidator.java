package shoppingmall.ankim.domain.memberLeave.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import shoppingmall.ankim.domain.memberLeave.controller.request.LeaveRequest;

public class LeaveMessageValidator implements ConstraintValidator<ValidLeaveMessage, LeaveRequest> {

    @Override
    public boolean isValid(LeaveRequest request, ConstraintValidatorContext context) {
        // "기타"인 경우 leaveMessage가 필수
        if ("기타".equals(request.getLeaveReason())) {
            return request.getLeaveMessage() != null && !request.getLeaveMessage().trim().isEmpty();
        }
        // 기타가 아닌 경우 leaveMessage는 null이어도 상관 없음
        return true;
    }
}