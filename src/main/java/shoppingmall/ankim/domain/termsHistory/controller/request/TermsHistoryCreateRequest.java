package shoppingmall.ankim.domain.termsHistory.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.termsHistory.service.request.TermsHistoryCreateServiceRequest;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class TermsHistoryCreateRequest {

    @NotBlank
    private Long termsNo;
    
    @NotBlank
    private String agreeYn;


    @Builder
    public TermsHistoryCreateRequest(Long termsNo, String agreeYn) {
        this.termsNo = termsNo;
        this.agreeYn = agreeYn;
    }

    // Service단 Reqeust로 변경
    public TermsHistoryCreateServiceRequest toServiceRequest() {
        return TermsHistoryCreateServiceRequest.builder()
                .termsNo(termsNo)
                .agreeYn(agreeYn)
                .build();
    }
    
}
