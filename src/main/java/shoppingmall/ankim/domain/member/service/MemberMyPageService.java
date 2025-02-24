package shoppingmall.ankim.domain.member.service;

import shoppingmall.ankim.domain.member.dto.MemberResponse;
import shoppingmall.ankim.domain.member.service.request.MemberRegisterServiceRequest;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsAgreement;

import java.util.List;

public interface MemberMyPageService {

    // 비밀번호 일치여부 검증
    void isValidPassword(String loginId, String password);
    MemberResponse getMemberInfo(String loginId);
}
