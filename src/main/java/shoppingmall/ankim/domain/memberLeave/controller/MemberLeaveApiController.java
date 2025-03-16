package shoppingmall.ankim.domain.memberLeave.controller;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.memberLeave.controller.request.LeaveRequest;
import shoppingmall.ankim.domain.memberLeave.service.MemberLeaveService;
import shoppingmall.ankim.domain.security.helper.SecurityContextHelper;
import shoppingmall.ankim.global.handler.LogoutHandler;
import shoppingmall.ankim.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/leave")
public class MemberLeaveApiController {
    private final MemberLeaveService memberLeaveService;
    private final SecurityContextHelper securityContextHelper;
    private final LogoutHandler logoutHandler;

    @PostMapping("/info")
    public ApiResponse<String> confirmPassword(
            @RequestBody LeaveRequest leaveRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String loginId = securityContextHelper.getLoginId();

        memberLeaveService.leaveMember(loginId, leaveRequest.toServiceRequest());
        logoutHandler.logout(request, response);

        return ApiResponse.ok("회원 탈퇴를 완료했습니다.");
    }
}