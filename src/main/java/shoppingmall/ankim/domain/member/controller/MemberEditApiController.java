package shoppingmall.ankim.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.member.controller.request.ChangePasswordRequest;
import shoppingmall.ankim.domain.member.service.MemberEditService;
import shoppingmall.ankim.domain.security.exception.CookieNotIncludedException;
import shoppingmall.ankim.domain.security.helper.SecurityContextHelper;
import shoppingmall.ankim.global.response.ApiResponse;

import static shoppingmall.ankim.global.exception.ErrorCode.COOKIE_NOT_INCLUDED;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/edit")
public class MemberEditApiController {

    private final MemberEditService memberEditService;
    private final SecurityContextHelper securityContextHelper;

    @PostMapping("/confirm-password")
    public ApiResponse<String> confirmPassword(
            @RequestParam String password
    ) {
        String loginId = securityContextHelper.getLoginId();

        memberEditService.isValidPassword(loginId, password);

        return ApiResponse.ok("비밀번호 검증에 성공했습니다.");
    }

    @PutMapping("/change-password")
    public ApiResponse<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @CookieValue(value = "access", required = false) String access
    ) {
        String loginId = securityContextHelper.getLoginId();

        // 비밀번호 변경
        memberEditService.changePassword(loginId, request.toServiceRequest());

        return ApiResponse.ok("");
    }
}
