package shoppingmall.ankim.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.member.controller.request.ChangePasswordRequest;
import shoppingmall.ankim.domain.member.service.MemberEditService;
import shoppingmall.ankim.domain.security.exception.CookieNotIncludedException;
import shoppingmall.ankim.global.response.ApiResponse;

import static shoppingmall.ankim.global.exception.ErrorCode.COOKIE_NOT_INCLUDED;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/edit")
public class MemberEditApiController {

    private final MemberEditService memberEditService;

    @PostMapping("/confirm-password")
    public ApiResponse<String> confirmPassword(
            @RequestParam String password
    ) {
        String loginId = getLoginId();

        memberEditService.isValidPassword(loginId, password);

        return ApiResponse.ok("비밀번호 검증에 성공했습니다.");
    }

    @PutMapping("/change-password")
    public ApiResponse<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @CookieValue(value = "access", required = false) String access
    ) {
        String loginId = getLoginId();

        // 비밀번호 변경
        memberEditService.changePassword(loginId, request.toServiceRequest());

        return ApiResponse.ok("");
    }


    // 쿠키에서 access 토큰이 넘어왔는지 확인하는 것 이므로 컨트롤러 단에 유지
    private static void isExistAccessToken(String access) {
        if (access == null) {
            throw new CookieNotIncludedException(COOKIE_NOT_INCLUDED);
        }
    }

    private static String getLoginId() {
        // SecurityContext에서 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName(); // 로그인 ID
    }

}
