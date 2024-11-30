package shoppingmall.ankim.domain.member.service;

import shoppingmall.ankim.domain.member.dto.MemberResponse;
import shoppingmall.ankim.domain.member.service.request.MemberRegisterServiceRequest;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsAgreement;

import java.util.List;

public interface MemberService { // FIXME 클래스명 변경(-> MemberJoinService)
    // 아이디(이메일) 중복 검증
    void isLoginIdDuplicated(String loginId);

    // 회원가입 로직
    MemberResponse registerMember(MemberRegisterServiceRequest request, List<TermsAgreement> termsAgreements);
}
