package shoppingmall.ankim.domain.member.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.member.controller.request.MemberEmailRequest;
import shoppingmall.ankim.domain.member.controller.request.MemberRegisterRequest;
import shoppingmall.ankim.domain.member.dto.MemberResponse;
import shoppingmall.ankim.domain.member.service.MemberService;
import shoppingmall.ankim.domain.member.service.request.MemberRegisterServiceRequest;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsAgreement;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
@SessionAttributes("termsAgreements")
public class MemberJoinApiController {

    private final MemberService memberService;

    // 사용가능한 이메일인지 검증한다.(중복 확인)
    @PostMapping("/email-check")
    public ApiResponse<String> existByEmail(@Valid @RequestBody MemberEmailRequest request) {
        // 이메일 중복 확인 로직
        memberService.isLoginIdDuplicated(request.getLoginId());
        return ApiResponse.ok("사용 가능한 이메일입니다.");
    }

    // 약관 동의 후 다음 회원가입 절차로 넘어간다.
    @PostMapping("/terms-next")
    public ApiResponse<String> termsAgreements(@RequestBody List<TermsAgreement> termsAgreements, HttpSession session) {
        session.setAttribute("termsAgreements", termsAgreements);

        // 약관 동의한 내용(termsAgreements)을 세션에 저장
        return ApiResponse.ok("약관 동의를 완료했습니다."); // FIXME 이메일 인증 페이지 필요
    }

    // 입력한 회원가입 정보를 등록한다.
    @PostMapping("/register")
    public ApiResponse<MemberResponse> register (@Valid @RequestBody MemberRegisterRequest request, Model model, HttpSession session) {
        // 회원가입 정보 - Member에 저장
        // 약관동의 정보 - termsAgreements(세션에 저장되어 있음)에서 데이터 꺼내서 입력

        MemberRegisterServiceRequest serviceRequest = request.toServiceRequest(); // serviceRequest로 변환

        List<TermsAgreement> termsAgreements = (List<TermsAgreement>) session.getAttribute("termsAgreements"); // 약관동의를 한 약관들 호출

        MemberResponse memberResponse = memberService.registerMember(serviceRequest, termsAgreements);

//        // memberResponse를 회원가입 완료 페이지에 전달
//        model.addAttribute("memberResponse", memberResponse);

        // 회원가입이 완료된 후 세션에서 약관동의 정보 삭제
        session.removeAttribute("termsAgreements");

        return ApiResponse.ok(memberResponse);
    }


}
