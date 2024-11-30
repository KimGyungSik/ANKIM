package shoppingmall.ankim.domain.member.service;

import shoppingmall.ankim.domain.member.dto.MemberResponse;
import shoppingmall.ankim.domain.member.service.request.ChangePasswordServiceRequest;
import shoppingmall.ankim.domain.member.service.request.MemberRegisterServiceRequest;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsAgreement;

import java.util.List;

public interface MemberMyPageService {

    // 비밀번호 일치여부 검증
    void isValidPassword(String accessToken, String password);

    // 비밀번호 변경
    void changePassword(String accessToken, ChangePasswordServiceRequest request);
}
