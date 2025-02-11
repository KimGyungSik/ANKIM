package shoppingmall.ankim.domain.terms.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.termsHistory.entity.TermsHistory;

@Getter
@NoArgsConstructor
public class TermsAgreeResponse {
    private Long no;
    private String name;
    private int termsVersion;
    private String agreeYn; // "Y" or "N"
    private Integer level; // 약관 레벨

    @Builder
    public TermsAgreeResponse(Long no, String name, int termsVersion, String agreeYn, Integer level) {
        this.no = no;
        this.name = name;
        this.termsVersion = termsVersion;
        this.agreeYn = agreeYn;
        this.level = level;
    }

    public static TermsAgreeResponse of(Terms terms, String agreeYn) {
        return TermsAgreeResponse.builder()
                .no(terms.getNo())
                .name(terms.getName())
                .termsVersion(terms.getTermsVersion())
                .agreeYn(agreeYn)
                .level(terms.getLevel())
                .build();
    }

}