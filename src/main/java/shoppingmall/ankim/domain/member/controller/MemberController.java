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
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.List;

import static shoppingmall.ankim.global.exception.ErrorCode.MISSING_REQUIRED_ID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/join")
    @ResponseBody
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

}
