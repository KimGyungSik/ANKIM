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
import shoppingmall.ankim.domain.security.helper.SecurityContextHelper;
import shoppingmall.ankim.global.response.ApiResponse;

import static shoppingmall.ankim.global.exception.ErrorCode.COOKIE_NOT_INCLUDED;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/address")
public class MemberAddressRegisterController {

    private final MemberAddressService memberAddressService;
    private final SecurityContextHelper securityContextHelper;

    @PutMapping("/edit")
    public ApiResponse<String> saveOrUpdateAddress(
            @Valid @RequestBody MemberAddressRegisterRequest request
    ) {
        String loginId = securityContextHelper.getLoginId();

        // Service
        String mesage = memberAddressService.saveOrUpdateAddress(loginId, request.toServiceRequest());

        return ApiResponse.ok(mesage);
    }
}
