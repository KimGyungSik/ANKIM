package shoppingmall.ankim.domain.memberLeave.service;

import shoppingmall.ankim.domain.memberLeave.service.request.LeaveServiceRequest;

public interface MemberLeaveService {
    void leaveMember(String loginId, LeaveServiceRequest request);
}
