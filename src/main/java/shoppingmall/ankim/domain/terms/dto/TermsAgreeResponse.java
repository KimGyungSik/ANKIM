package shoppingmall.ankim.domain.terms.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.termsHistory.entity.TermsHistory;

@Getter
@NoArgsConstructor
public class TermsAgreeResponse {
    private Long termsNo;
    private Long terms_hist_no;
    private String name;
    private String contents;
    private int termsVersion;
    private String agreeYn; // "Y" or "N"
    private Integer level; // 약관 레벨
    private Long parentsNo;// 부모 약관

    @Builder
    public TermsAgreeResponse(Long termsNo, Long terms_hist_no, String name, String contents, int termsVersion, String agreeYn, Integer level, Long parentsNo) {
        this.termsNo = termsNo;
        this.terms_hist_no = terms_hist_no;
        this.name = name;
        this.contents = contents;
        this.termsVersion = termsVersion;
        this.agreeYn = agreeYn;
        this.level = level;
        this.parentsNo = parentsNo;
    }

    public static TermsAgreeResponse of(Terms terms, String agreeYn) {
        return TermsAgreeResponse.builder()
                .termsNo(terms.getNo())
                .name(terms.getName())
                .contents(terms.getContents())
                .termsVersion(terms.getTermsVersion())
                .agreeYn(agreeYn)
                .level(terms.getLevel())
                .parentsNo(terms.getParentTerms().getNo())
                .build();
    }

    public static TermsAgreeResponse of(TermsHistory termshist, String agreeYn) {
        return TermsAgreeResponse.builder()
                .termsNo(termshist.getTerms().getNo())
                .terms_hist_no(termshist.getNo())
                .name(termshist.getTerms().getName())
                .contents(termshist.getTerms().getContents())
                .termsVersion(termshist.getTerms().getTermsVersion())
                .agreeYn(agreeYn)
                .level(termshist.getTerms().getLevel())
                .parentsNo(termshist.getTerms().getParentTerms().getNo())
                .build();
    }

}