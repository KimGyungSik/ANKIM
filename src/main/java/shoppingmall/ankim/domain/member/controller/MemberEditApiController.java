package shoppingmall.ankim.domain.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.member.controller.request.ChangePasswordRequest;
import shoppingmall.ankim.domain.member.controller.request.PasswordRequest;
import shoppingmall.ankim.domain.member.dto.MemberInfoResponse;
import shoppingmall.ankim.domain.member.service.MemberEditService;
import shoppingmall.ankim.domain.security.helper.SecurityContextHelper;
import shoppingmall.ankim.global.handler.LogoutHandler;
import shoppingmall.ankim.global.response.ApiResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/edit")
public class MemberEditApiController {

    private final MemberEditService memberEditService;
    private final SecurityContextHelper securityContextHelper;
    private final LogoutHandler logoutHandler;

    @PostMapping("/confirm-password")
    public ApiResponse<String> confirmPassword(
            @RequestBody @Valid PasswordRequest request
    ) {
        String loginId = securityContextHelper.getLoginId();

        memberEditService.isValidPassword(loginId, request.toServiceRequest());

        return ApiResponse.ok("비밀번호 검증에 성공했습니다.");
    }

    // 마이 페이지의 회원정보수정페이지에 들어갈때 개인정보 읽어오기 ( R ) // FIXME
    @GetMapping()
    public ApiResponse<MemberInfoResponse> getMyInfo() {
        String loginId = securityContextHelper.getLoginId();

        MemberInfoResponse response = memberEditService.getMemberInfo(loginId);

        return ApiResponse.ok(response);
    }

    @PutMapping("/change-password")
    public ApiResponse<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String loginId = securityContextHelper.getLoginId();

        // 비밀번호 변경
        memberEditService.changePassword(loginId, changePasswordRequest.toServiceRequest());

        // 로그아웃 진행
        logoutHandler.logout(request, response);

        return ApiResponse.ok("비밀번호가 변경되었으며 로그아웃되었습니다.");
    }
}
