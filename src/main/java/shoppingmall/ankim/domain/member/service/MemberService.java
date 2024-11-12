package shoppingmall.ankim.domain.member.service;

import shoppingmall.ankim.domain.member.dto.MemberResponse;
import shoppingmall.ankim.domain.member.service.request.MemberRegisterServiceRequest;

public interface MemberService {
    // 아이디(이메일) 중복 검증
    void emailCheck(String id);

    // 회원가입 로직
    MemberResponse registerMember(MemberRegisterServiceRequest request);
}
