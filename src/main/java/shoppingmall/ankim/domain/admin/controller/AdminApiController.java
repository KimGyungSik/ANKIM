package shoppingmall.ankim.domain.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.admin.controller.request.AdminIdValidRequest;
import shoppingmall.ankim.domain.admin.controller.request.AdminRegisterRequest;
import shoppingmall.ankim.domain.admin.service.AdminService;
import shoppingmall.ankim.global.response.ApiResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminApiController {

    private final AdminService adminService;

    @PostMapping("/check-login-id")
    public ApiResponse<String> checkLoginId(@Valid @RequestBody AdminIdValidRequest request) {
        // 이메일 중복 확인 로직
        adminService.isLoginIdDuplicated(request.toServiceRequest());
        return ApiResponse.ok("사용 가능한 아이디입니다.");
    }

    // 입력한 회원가입 정보를 등록한다.
    @PostMapping("/register")
    public ApiResponse<String> register(@Valid @RequestBody AdminRegisterRequest request) {
        log.info("Admin registration request received: {}", request);
        adminService.register(request.toServiceRequest());

        return ApiResponse.ok("회원가입을 완료했습니다."); // FIXME 회원가입 후 이동할 관리자 페이지
    }
}
