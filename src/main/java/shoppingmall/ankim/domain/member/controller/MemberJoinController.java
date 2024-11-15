package shoppingmall.ankim.domain.member.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.member.controller.request.MemberEmailRequest;
import shoppingmall.ankim.domain.member.controller.request.MemberRegisterRequest;
import shoppingmall.ankim.domain.member.dto.MemberResponse;
import shoppingmall.ankim.domain.member.exception.MemberRegistrationException;
import shoppingmall.ankim.domain.member.service.MemberService;
import shoppingmall.ankim.domain.member.service.request.MemberRegisterServiceRequest;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsAgreement;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.List;

import static shoppingmall.ankim.global.exception.ErrorCode.MISSING_REQUIRED_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
@SessionAttributes("termsAgreements")
public class MemberJoinController {

    private final MemberService memberService;

    // 사용가능한 이메일인지 검증한다.(중복 확인)
    @PostMapping("/email-check")
    public ApiResponse<String> existByEmail(@Valid @RequestBody MemberEmailRequest request) {
        // 이메일 중복 확인 로직
        memberService.loginIdCheck(request.getId());
        return ApiResponse.ok("사용 가능한 이메일입니다.");
    }

    // 약관 동의 후 다음 회원가입 절차로 넘어간다.
    @PostMapping("/terms-next")
    public ApiResponse<String> nextRegisterEmail(@RequestBody List<TermsAgreement> termsAgreements, HttpSession session) {
        session.setAttribute("termsAgreements", termsAgreements);

        // 약관 동의한 내용(termsAgreements)을 세션에 저장
        return ApiResponse.ok("약관 동의를 완료했습니다."); // FIXME 이메일 인증 페이지 필요
    }


}
