package shoppingmall.ankim.domain.termsHistory.service.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.address.entity.BaseAddress;
import shoppingmall.ankim.domain.admin.entity.Admin;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.termsHistory.entity.TermsHistory;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TermsUpdateServiceRequest {

    private Long terms_no;
    private Long terms_hist_no;
    private String terms_hist_agreeYn;

    @Builder
    public TermsUpdateServiceRequest(Long terms_no, Long terms_hist_no, String terms_hist_agreeYn) {
        this.terms_no = terms_no;
        this.terms_hist_no = terms_hist_no;
        this.terms_hist_agreeYn = terms_hist_agreeYn;
    }

    public TermsHistory toEntity(Member member, Terms terms, LocalDateTime now) {
        return TermsHistory.builder()
                .member(member)
                .terms(terms)
                .agreeYn(this.terms_hist_agreeYn) // 약관 동의(Y), 철회(N)
                .agreeDate(now)
                .build();
    }

    public TermsHistory toEntity(Member member, Terms terms, String activeYn) {
        return TermsHistory.builder()
            .member(member)
            .terms(terms)
            .agreeYn(this.terms_hist_agreeYn)
            .agreeDate(LocalDateTime.now())
            .activeYn(activeYn)
            .build();
    }

}
