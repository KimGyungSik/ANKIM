package shoppingmall.ankim.domain.member.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.cart.dto.CartItemsResponse;
import shoppingmall.ankim.domain.member.controller.request.PasswordRequest;
import shoppingmall.ankim.domain.member.dto.MemberInfoResponse;
import shoppingmall.ankim.domain.member.dto.MemberResponse;
import shoppingmall.ankim.domain.member.service.MemberMyPageService;
import shoppingmall.ankim.domain.security.helper.SecurityContextHelper;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.List;
import java.util.Map;

import static shoppingmall.ankim.global.constants.ShippingConstants.FREE_SHIPPING_THRESHOLD;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/mypage")
public class MemberMyPageApiController {

    private final MemberMyPageService memberMyPageService;
    private final SecurityContextHelper securityContextHelper;

    // 마이 페이지에 들어갈때 개인정보 읽어오기 ( R ) // FIXME 테스트용으로 작성하여서 수정필요
    @GetMapping
    public ApiResponse<MemberResponse> getMyPage() {
        String loginId = securityContextHelper.getLoginId();

        log.info("loginId: {}", loginId);

        // 회원 데이터 조회 (가상의 예제)
        MemberResponse memberData = MemberResponse.builder()
                .name("홍*동")
                .build();

        return ApiResponse.ok(memberData);
    }

    @PostMapping("/confirm-password") // FIXME 마이 페이지 비밀번호 재확인
    public ApiResponse<String> confirmPassword(
            @RequestBody PasswordRequest passwordRequest
    ) {
        String loginId = securityContextHelper.getLoginId();

        memberMyPageService.isValidPassword(loginId, passwordRequest.getPassword());

        return ApiResponse.ok("비밀번호 검증에 성공했습니다.");
    }

}
