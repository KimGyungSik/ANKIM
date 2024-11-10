package shoppingmall.ankim.domain.termsHistory.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shoppingmall.ankim.domain.termsHistory.service.TermsHistoryService;
import shoppingmall.ankim.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/terms-history")
public class TermsHistoryController {

    // 약관 동의 처리
    @PostMapping("/aggree")
    public ApiResponse<String> termsAgree() {


        return ApiResponse.ok("OK");
    }
}
