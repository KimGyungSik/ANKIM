package shoppingmall.ankim.domain.address.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.address.controller.request.MemberAddressRegisterRequest;
import shoppingmall.ankim.domain.address.service.MemberAddressService;
import shoppingmall.ankim.domain.address.service.MemberAddressServiceImpl;
import shoppingmall.ankim.domain.security.exception.CookieNotIncludedException;
import shoppingmall.ankim.global.response.ApiResponse;

import static shoppingmall.ankim.global.exception.ErrorCode.COOKIE_NOT_INCLUDED;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/address")
public class MemberAddressRegisterController {

    private final MemberAddressService memberAddressService;

    @PutMapping("/edit")
    public ApiResponse<String> saveOrUpdateAddress(
            @Valid @RequestBody MemberAddressRegisterRequest request
    ) {
        String loginId = getLoginId();

        // Service
        String mesage = memberAddressService.saveOrUpdateAddress(loginId, request.toServiceRequest());

        return ApiResponse.ok(mesage);
    }

    private static String getLoginId() {
        // SecurityContext에서 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName(); // 로그인 ID
    }
}
