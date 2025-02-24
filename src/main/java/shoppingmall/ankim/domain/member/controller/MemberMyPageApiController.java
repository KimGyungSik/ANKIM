package shoppingmall.ankim.domain.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.cart.dto.CartItemsResponse;
import shoppingmall.ankim.domain.member.controller.request.PasswordRequest;
import shoppingmall.ankim.domain.member.dto.MemberInfoResponse;
import shoppingmall.ankim.domain.member.dto.MemberResponse;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.exception.InvalidMemberException;
import shoppingmall.ankim.domain.member.service.MemberMyPageService;
import shoppingmall.ankim.domain.security.helper.SecurityContextHelper;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.List;
import java.util.Map;

import static shoppingmall.ankim.global.constants.ShippingConstants.FREE_SHIPPING_THRESHOLD;
import static shoppingmall.ankim.global.exception.ErrorCode.INVALID_MEMBER;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/mypage")
public class MemberMyPageApiController {

    private final MemberMyPageService memberMyPageService;
    private final SecurityContextHelper securityContextHelper;

    // 마이 페이지에 들어갈때 개인정보 읽어오기 ( R )
    @GetMapping
    public ApiResponse<MemberResponse> getMyPage() {
        String loginId = securityContextHelper.getLoginId();

        MemberResponse memberData = memberMyPageService.getMemberInfo(loginId);

        return ApiResponse.ok(memberData);
    }

    @PostMapping("/confirm-password")
    public ApiResponse<String> confirmPassword(
            @RequestBody PasswordRequest passwordRequest
    ) {
        String loginId = securityContextHelper.getLoginId();

        memberMyPageService.isValidPassword(loginId, passwordRequest.getPassword());

        return ApiResponse.ok("비밀번호 검증에 성공했습니다.");
    }


}
