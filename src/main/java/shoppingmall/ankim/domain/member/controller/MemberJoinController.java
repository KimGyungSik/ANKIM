package shoppingmall.ankim.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.member.controller.request.MemberEmailRequest;
import shoppingmall.ankim.domain.member.controller.request.MemberRegisterRequest;
import shoppingmall.ankim.domain.member.exception.MemberRegistrationException;
import shoppingmall.ankim.domain.member.service.MemberService;
import shoppingmall.ankim.global.advice.GlobalExceptionAdvice;
import shoppingmall.ankim.global.response.ApiResponse;

import static shoppingmall.ankim.global.exception.ErrorCode.MISSING_REQUIRED_ID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberJoinController {

    private final MemberService memberService;

    @PostMapping("/email-check")
    public ApiResponse<String> existByEmail(@Valid @RequestBody MemberEmailRequest request) {
        // 이메일 중복 확인 로직
        memberService.emailCheck(request.getId());
        return ApiResponse.ok("사용 가능한 이메일입니다.");
    }

    @PostMapping("/email-next")
    public String nextRegisterStep(@RequestParam("id") String id, Model model) {
        // id값이 전달되지 않았으면 사용자 정의 예외 발생
        if(id == null || id.isEmpty()){
            throw new MemberRegistrationException(MISSING_REQUIRED_ID);
        }
        // id값이 잘 전달됐으면 다음페이지로 이동
        model.addAttribute("id", id);
        return "registerNext"; // 다음 입력 페이지 (예: registerNext.html) -> 작성 필요
    }

    @PostMapping("/register")
    public ApiResponse<String> registerMember(@Valid @RequestBody MemberRegisterRequest request, BindingResult bindingResult) {
        // Validation 검사
        if (bindingResult.hasErrors()) {
            // 오류 메시지 반환
        }

        // Validation 통과 후 비즈니스 로직 호출
        return ApiResponse.ok("");
    }

}
