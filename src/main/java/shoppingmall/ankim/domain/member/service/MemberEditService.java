package shoppingmall.ankim.domain.member.service;

import shoppingmall.ankim.domain.member.controller.request.PasswordRequest;
import shoppingmall.ankim.domain.member.service.request.ChangePasswordServiceRequest;
import shoppingmall.ankim.domain.member.service.request.PasswordServiceRequest;

public interface MemberEditService {

    // 비밀번호 일치여부 검증
    void isValidPassword(String loginId, PasswordServiceRequest request);

    // 비밀번호 변경
    void changePassword(String loginId, ChangePasswordServiceRequest request);
}
