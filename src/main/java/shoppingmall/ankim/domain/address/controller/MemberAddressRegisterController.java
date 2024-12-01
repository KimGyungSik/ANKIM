package shoppingmall.ankim.domain.address.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
            @CookieValue(value = "access", required = false) String access,
            @Valid @RequestBody MemberAddressRegisterRequest request
    ) {
        isExistAccessToken(access);

        // Service
        String mesage = memberAddressService.saveOrUpdateAddress(access, request.toServiceRequest());

        return ApiResponse.ok(mesage);
    }

    // 쿠키에서 access 토큰이 넘어왔는지 확인하는 것 이므로 컨트롤러 단에 유지
    private static void isExistAccessToken(String access) {
        if (access == null) {
            throw new CookieNotIncludedException(COOKIE_NOT_INCLUDED);
        }
    }
}
