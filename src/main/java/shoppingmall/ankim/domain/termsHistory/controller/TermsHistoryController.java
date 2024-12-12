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
import shoppingmall.ankim.domain.security.helper.SecurityContextHelper;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsHistoryCreateRequest;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsUpdateRequest;
import shoppingmall.ankim.domain.termsHistory.dto.TermsHistoryUpdateResponse;
import shoppingmall.ankim.domain.termsHistory.exception.EmptyTermsUpdateRequestException;
import shoppingmall.ankim.domain.termsHistory.service.TermsHistoryService;
import shoppingmall.ankim.domain.termsHistory.service.request.TermsUpdateServiceRequest;
import shoppingmall.ankim.global.response.ApiResponse;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import static shoppingmall.ankim.global.exception.ErrorCode.EMPTY_TERMS_UPDATE_REQUEST;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/terms")
public class TermsHistoryController {

    private final TermsHistoryService termsHistoryService;
    private final SecurityContextHelper securityContextHelper;

    // 약관 동의 처리
    @PostMapping("/update")
    public ApiResponse<TermsHistoryUpdateResponse> termsAgree(@Valid @RequestBody List<TermsUpdateRequest> request) {
        String loginId = securityContextHelper.getLoginId();

        if(request == null || request.isEmpty()){
            throw new EmptyTermsUpdateRequestException(EMPTY_TERMS_UPDATE_REQUEST);
        }

        // service
        List<TermsUpdateServiceRequest> serviceRequestList = new ArrayList<>();
        for (TermsUpdateRequest termsUpdateRequest : request) {
            serviceRequestList.add(termsUpdateRequest.toServiceRequest());
        }
        TermsHistoryUpdateResponse termsHistoryUpdateResponse = termsHistoryService.updateTermsAgreement(loginId, serviceRequestList);

        return ApiResponse.ok(termsHistoryUpdateResponse);
    }
}
