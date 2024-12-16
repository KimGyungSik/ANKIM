package shoppingmall.ankim.domain.leave.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.leave.controller.request.LeaveRequest;
import shoppingmall.ankim.domain.security.helper.SecurityContextHelper;
import shoppingmall.ankim.global.response.ApiResponse;
@RestController
@RequiredArgsConstructor
@RequestMapping("api/leave")
public class LeaveController {
    private final SecurityContextHelper securityContextHelper;
    @PostMapping("/info")
    public ApiResponse<String> confirmPassword(
            @RequestBody LeaveRequest leaveRequest
    ) {
        String loginId = securityContextHelper.getLoginId();
        return ApiResponse.ok("비밀번호 검증에 성공했습니다.");
    }
}