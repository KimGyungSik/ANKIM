package shoppingmall.ankim.domain.termsHistory.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shoppingmall.ankim.domain.email.controller.request.MailRequest;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsHistoryCreateRequest;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsUpdateRequest;
import shoppingmall.ankim.domain.termsHistory.dto.TermsHistoryUpdateResponse;
import shoppingmall.ankim.domain.termsHistory.service.TermsHistoryService;
import shoppingmall.ankim.domain.termsHistory.service.request.TermsUpdateServiceRequest;
import shoppingmall.ankim.global.response.ApiResponse;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/terms")
public class TermsHistoryController {

    private final TermsHistoryService termsHistoryService;

    // 약관 동의 처리
    @PostMapping("/update")
    public ApiResponse<TermsHistoryUpdateResponse> termsAgree(@Valid @RequestBody List<TermsUpdateRequest> request) {
        String loginId = getLoginId();

        // service
        List<TermsUpdateServiceRequest> serviceRequestList = new ArrayList<>();
        for (TermsUpdateRequest termsUpdateRequest : request) {
            serviceRequestList.add(termsUpdateRequest.toServiceRequest());
        }
        TermsHistoryUpdateResponse termsHistoryUpdateResponse = termsHistoryService.updateTermsAgreement(loginId, serviceRequestList);

        return ApiResponse.ok(termsHistoryUpdateResponse);
    }

    private static String getLoginId() {
        // SecurityContext에서 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName(); // 로그인 ID
    }
}
