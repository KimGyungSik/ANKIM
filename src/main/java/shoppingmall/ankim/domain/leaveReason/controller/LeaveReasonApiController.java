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
import shoppingmall.ankim.domain.terms.dto.TermsLeaveResponse;
import shoppingmall.ankim.domain.terms.service.TermsService;
import shoppingmall.ankim.domain.terms.service.query.TermsQueryService;
import shoppingmall.ankim.domain.terms.service.query.TermsQueryServiceImpl;
import shoppingmall.ankim.global.handler.LogoutHandler;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static shoppingmall.ankim.global.util.MaskingUtil.maskLoginId;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/leaveReason")
public class LeaveReasonApiController {

    private final SecurityContextHelper securityContextHelper;
    private final LeaveReasoneService leaveReasoneService;
    private final TermsQueryService termsQueryService;

    // 회원 탈퇴 사유 로딩
    @GetMapping()
    public ApiResponse<Map<String, Object>> getLeaveReason() {

        List<LeaveReasonResponse> reason = leaveReasoneService.getReason();
        List<TermsLeaveResponse> leaveTerm = termsQueryService.findLeaveTerm();

        Map<String, Object> map = new HashMap<>();
        map.put("reason", reason);
        map.put("leaveTerm", leaveTerm);

        return ApiResponse.ok(map);
    }
}
