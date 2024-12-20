package shoppingmall.ankim.domain.member.service;

import shoppingmall.ankim.domain.member.service.request.ChangePasswordServiceRequest;

public interface MemberEditService {

    // 비밀번호 일치여부 검증
    void isValidPassword(String loginId, String password);

    // 비밀번호 변경
    void changePassword(String loginId, ChangePasswordServiceRequest request);
}
