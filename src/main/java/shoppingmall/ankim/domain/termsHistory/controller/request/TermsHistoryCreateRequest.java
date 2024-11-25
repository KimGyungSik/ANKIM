package shoppingmall.ankim.domain.termsHistory.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import shoppingmall.ankim.domain.termsHistory.service.request.TermsHistoryCreateServiceRequest;

import java.time.LocalDate;

public class TermsHistoryCreateRequest {

    @NotBlank
    private Long memNo;
    
    @NotBlank
    private Long termsNo;
    
    @NotBlank
    private String agreeYn;

    private LocalDate agreeDate;

    @Builder
    public TermsHistoryCreateRequest(Long memNo, Long termsNo, String agreeYn) {
        this.memNo = memNo;
        this.termsNo = termsNo;
        this.agreeYn = agreeYn;
        this.agreeDate = LocalDate.now();
    }

    // Service단 Reqeust로 변경
    public TermsHistoryCreateServiceRequest toServiceRequest() {
        return TermsHistoryCreateServiceRequest.builder()
                .memNo(memNo)
                .termsNo(termsNo)
                .agreeYn(agreeYn)
                .agreeDate(agreeDate)
                .build();
    }
    
}
