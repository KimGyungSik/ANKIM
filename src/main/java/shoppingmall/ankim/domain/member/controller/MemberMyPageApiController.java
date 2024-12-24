package shoppingmall.ankim.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.member.controller.request.PasswordRequest;
import shoppingmall.ankim.domain.member.service.MemberMyPageService;
import shoppingmall.ankim.domain.security.helper.SecurityContextHelper;
import shoppingmall.ankim.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/mypage")
public class MemberMyPageApiController {

    private final MemberMyPageService memberMyPageService;
    private final SecurityContextHelper securityContextHelper;

    @PostMapping("/confirm-password") // FIXME 마이 페이지 비밀번호 재확인
    public ApiResponse<String> confirmPassword(
            @RequestBody PasswordRequest passwordRequest
    ) {
        String loginId = securityContextHelper.getLoginId();

        memberMyPageService.isValidPassword(loginId, passwordRequest.getPassword());

        return ApiResponse.ok("비밀번호 검증에 성공했습니다.");
    }

}
