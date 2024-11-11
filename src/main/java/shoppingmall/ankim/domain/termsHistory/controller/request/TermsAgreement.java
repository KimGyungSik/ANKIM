package shoppingmall.ankim.domain.termsHistory.controller.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class TermsAgreement {

    private Long termNo; // 약관 번호
    private String termName; // 약관명
    private String agreeYn; // 약관 동의 여부
    private Integer level; // 약관 레벨
    private String termsYn; // 필수동의 여부(필수 Y, 선택 N)

    @Builder
    public TermsAgreement(Long termNo, String termName, String agreeYn, Integer level, String termsYn) {
        this.termNo = termNo;
        this.termName = termName;
        this.agreeYn = agreeYn;
        this.level = level;
        this.termsYn = termsYn;
    }

}
