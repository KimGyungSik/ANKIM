package shoppingmall.ankim.domain.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.admin.controller.request.AdminIdValidRequest;
import shoppingmall.ankim.domain.admin.service.AdminService;
import shoppingmall.ankim.global.response.ApiResponse;

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
}
