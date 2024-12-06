package shoppingmall.ankim.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.member.controller.request.PasswordRequest;
import shoppingmall.ankim.domain.member.service.MemberMyPageService;
import shoppingmall.ankim.domain.member.service.MemberService;
import shoppingmall.ankim.domain.security.exception.CookieNotIncludedException;
import shoppingmall.ankim.global.response.ApiResponse;

import static shoppingmall.ankim.global.exception.ErrorCode.COOKIE_NOT_INCLUDED;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/mypage")
public class MemberMyPageApiController {

    private final MemberMyPageService memberMyPageService;

    @PostMapping("/confirm-password") // FIXME 마이 페이지 비밀번호 재확인
    public ApiResponse<String> confirmPassword(
            @RequestBody PasswordRequest passwordRequest
    ) {
        String loginId = getLoginId();

        memberMyPageService.isValidPassword(loginId, passwordRequest.getPwd());

        return ApiResponse.ok("비밀번호 검증에 성공했습니다.");
    }

    private static String getLoginId() {
        // SecurityContext에서 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName(); // 로그인 ID
    }

}
