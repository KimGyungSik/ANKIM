package shoppingmall.ankim.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.member.controller.request.MemberEmailRequest;
import shoppingmall.ankim.domain.member.controller.request.MemberRegisterRequest;
import shoppingmall.ankim.domain.member.exception.MemberRegistrationException;
import shoppingmall.ankim.domain.member.service.MemberService;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsAggrement;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.List;

import static shoppingmall.ankim.global.exception.ErrorCode.MISSING_REQUIRED_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberJoinController {

    private final MemberService memberService;

    // 사용가능한 이메일인지 검증한다.(중복 확인)
    @PostMapping("/email-check")
    public ApiResponse<String> existByEmail(@Valid @RequestBody MemberEmailRequest request) {
        // 이메일 중복 확인 로직
        memberService.emailCheck(request.getId());
        return ApiResponse.ok("사용 가능한 이메일입니다.");
    }

    // 약관 동의 후 다음 회원가입 절차로 넘어간다.
    @PostMapping("/terms-next")
    public String nextRegisterEmail(@RequestBody List<TermsAggrement> termsAggrements, Model model) {

        return "registerNext"; // 다음 입력 페이지 (예: registerNext.html) -> 작성 필요
    }

    // 이메일 입력 후 다음 회원가입 절차로 넘어간다.
    @PostMapping("/email-next")
    public String nextRegisterMember(@RequestParam("id") String id, Model model) {
        // id값이 전달되지 않았으면 사용자 정의 예외 발생
        if(id == null || id.isEmpty()){
            throw new MemberRegistrationException(MISSING_REQUIRED_ID);
        }
        // id값이 잘 전달됐으면 다음페이지로 이동
        model.addAttribute("id", id);
        return "registerNext"; // 다음 입력 페이지 (예: registerNext.html) -> 작성 필요
    }

    // 입력한 회원가입 정보를 등록한다.
    @PostMapping("/register")
    public ApiResponse<String> registerMember(@Valid @RequestBody MemberRegisterRequest request) {
        // 회원가입 정보 - Member
        // 약관동의 정보 - TermsHistory

        // Validation 통과 후 비즈니스 로직 호출
        return ApiResponse.ok("");
    }

}
