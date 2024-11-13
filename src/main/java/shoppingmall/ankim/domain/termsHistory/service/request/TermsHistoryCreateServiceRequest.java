package shoppingmall.ankim.domain.termsHistory.service.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.termsHistory.entity.TermsHistory;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TermsHistoryCreateServiceRequest {

    @NotBlank
    private Long memNo;

    @NotBlank
    private Long termsNo;

    @NotBlank
    private String termsVer;

    @NotBlank
    private String agreeYn;

    private LocalDate agreeDate;

    @Builder
    public TermsHistoryCreateServiceRequest(Long memNo, Long termsNo, String agreeYn, LocalDate agreeDate) {
        this.memNo = memNo;
        this.termsNo = termsNo;
        this.agreeYn = agreeYn;
        this.agreeDate = agreeDate;
    }

    // serviceRequest를 Member 엔티티로 변환해서 회원가입할 때 사용
    public TermsHistory create() {
        return TermsHistory.builder()
//                .member(this.memNo)
//                .terms(this.termsNo)
                .agreeYn(this.agreeYn)
                .agreeDate(LocalDateTime.now())
                .build();
    }
}
