package shoppingmall.ankim.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.termsHistory.entity.TermsHistory;

@Data
@NoArgsConstructor
public class TermsAgreementResponse {

    private Long termsNo;   // 약관 번호
    private String name;     // 약관명
    private String contents; // 약관 내용
    private String termsYn;  // 필수 여부
    private Long termsHistoryNo; // 동의 이력 번호
    private String agreeYn;  // 동의 여부

    @Builder
    public TermsAgreementResponse(Long termsNo, String name, String contents, String termsYn, Long termsHistoryNo, String agreeYn) {
        this.termsNo = termsNo;
        this.name = name;
        this.contents = contents;
        this.termsYn = termsYn;
        this.termsHistoryNo = termsHistoryNo;
        this.agreeYn = agreeYn;
    }

    public static TermsAgreementResponse of(TermsHistory termsHistory) {
        return TermsAgreementResponse.builder()
                .termsNo(termsHistory.getTerms().getNo())
                .name(termsHistory.getTerms().getName())
                .contents(termsHistory.getTerms().getContents())
                .termsYn(termsHistory.getTerms().getTermsYn())
                .termsHistoryNo(termsHistory.getNo())
                .agreeYn(termsHistory.getAgreeYn())
                .build();
    }

}
