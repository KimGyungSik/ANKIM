package shoppingmall.ankim.domain.member.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.member.controller.request.MemberRegisterRequest;
import shoppingmall.ankim.domain.member.dto.MemberResponse;
import shoppingmall.ankim.domain.member.exception.MemberRegistrationException;
import shoppingmall.ankim.domain.member.service.MemberService;
import shoppingmall.ankim.domain.member.service.request.MemberRegisterServiceRequest;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsAgreement;
import shoppingmall.ankim.global.exception.ErrorCode;

import java.util.List;

import static shoppingmall.ankim.global.exception.ErrorCode.MISSING_REQUIRED_ID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/join")
    public String joinProcess() {

        return "welcome"; // FIXME 회원가입 페이지 작성 필요
    }

    // 이메일 입력 후 다음 회원가입 절차로 넘어간다.
    @GetMapping("/email-next")
    public String nextRegisterMember(@RequestParam("loginId") String loginId, Model model) {
        // id값이 전달되지 않았으면 사용자 정의 예외 발생
        if (loginId == null || loginId.isEmpty()) {
            throw new MemberRegistrationException(MISSING_REQUIRED_ID);
        }
        // id값이 잘 전달됐으면 다음페이지로 이동
        model.addAttribute("loginId", loginId);
        return "personalInfo"; // FIXME 회원 개인정보 입력 페이지 생성 필요
    }

    // 입력한 회원가입 정보를 등록한다.
    @PostMapping("/register")
    public String registerMember(@Valid @ModelAttribute MemberRegisterRequest request, Model model, HttpSession session) {
        // 회원가입 정보 - Member에 저장
        // 약관동의 정보 - termsAgreements(세션에 저장되어 있음)에서 데이터 꺼내서 입력

        MemberRegisterServiceRequest serviceRequest = request.toServiceRequest(); // serviceRequest로 변환

        List<TermsAgreement> termsAgreements = (List<TermsAgreement>) session.getAttribute("termsAgreements"); // 약관동의를 한 약관들 호출

        MemberResponse memberResponse = memberService.registerMember(serviceRequest, termsAgreements);

        // memberResponse를 회원가입 완료 페이지에 전달
        model.addAttribute("memberResponse", memberResponse);

        // 회원가입이 완료된 후 세션에서 약관동의 정보 삭제
        session.removeAttribute("termsAgreements");

        return "registerComplete"; // FIXME 회원가입 완료 페이지 작성 필요
    }
}
