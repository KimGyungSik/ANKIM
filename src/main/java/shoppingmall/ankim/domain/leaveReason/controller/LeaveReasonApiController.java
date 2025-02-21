package shoppingmall.ankim.domain.leaveReason.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shoppingmall.ankim.domain.leaveReason.dto.LeaveReasonResponse;
import shoppingmall.ankim.domain.leaveReason.repository.LeaveReasonRepository;
import shoppingmall.ankim.domain.leaveReason.service.LeaveReasoneService;
import shoppingmall.ankim.domain.member.dto.MemberInfoResponse;
import shoppingmall.ankim.domain.memberLeave.service.MemberLeaveService;
import shoppingmall.ankim.domain.security.helper.SecurityContextHelper;
import shoppingmall.ankim.global.handler.LogoutHandler;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/leaveReason")
public class LeaveReasonApiController {

    private final SecurityContextHelper securityContextHelper;
    private final LeaveReasoneService leaveReasoneService;

    // 회원 탈퇴 사유 로딩
    @GetMapping()
    public ApiResponse<List<LeaveReasonResponse>> getLeaveReason() {
        securityContextHelper.getLoginId();

        List<LeaveReasonResponse> response = leaveReasoneService.getReason();

        for (LeaveReasonResponse leaveReasonResponse : response) {
            System.out.println("leaveReasonResponse = " + leaveReasonResponse);
        }

        return ApiResponse.ok(response);
    }
}
