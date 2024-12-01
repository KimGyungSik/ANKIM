package shoppingmall.ankim.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
            @RequestParam String password,
            @CookieValue(value = "access", required = false) String access
    ) {
        isExistAccessToken(access);

        memberEditService.isValidPassword(access, password);

        return ApiResponse.ok("비밀번호 검증에 성공했습니다.");
    }

    @PutMapping("/change-password")
    public ApiResponse<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @CookieValue(value = "access", required = false) String access
    ) {
        isExistAccessToken(access);

        // 비밀번호 변경
        memberEditService.changePassword(access, request.toServiceRequest());

        return ApiResponse.ok("");
    }

    /*
    * 주소 정보 수정
    * - 회원이 새로운 주소를 등록하거나, 기존의 기본 주소를 수정할 때 사용
    * */
    @PostMapping
    public ApiResponse<String> saveOrUpdateAddress(
            @CookieValue(value = "access", required = false) String access
//            ,
//            @Valid @RequestBody MemberAddressRequest request
    ) {
//        validateAccessToken(access); // 쿠키 유효성 확인

        // 주소 등록/수정
//        memberAddressService.saveOrUpdateAddress(access, request);

        return ApiResponse.ok("주소가 성공적으로 저장되었습니다.");
    }




    // 쿠키에서 access 토큰이 넘어왔는지 확인하는 것 이므로 컨트롤러 단에 유지
    private static void isExistAccessToken(String access) {
        if (access == null) {
            throw new CookieNotIncludedException(COOKIE_NOT_INCLUDED);
        }
    }

}
