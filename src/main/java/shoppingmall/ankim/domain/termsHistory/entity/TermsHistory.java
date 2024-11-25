package shoppingmall.ankim.domain.termsHistory.entity;

import jakarta.persistence.*;
import lombok.*;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsAgreement;
import shoppingmall.ankim.global.audit.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "terms_history")
public class TermsHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "no")
    private Long no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mem_no", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terms_no", nullable = false)
    private Terms terms;

    @Column(name = "agree_yn", length = 1, nullable = false)
    private String agreeYn; // 동의 여부

    @Column(name = "agree_date", nullable = false)
    private LocalDateTime agreeDate; // 동의 일자

    @Builder
    public TermsHistory(Member member, Terms terms, String agreeYn, LocalDateTime agreeDate) {
        this.member = member;
        this.terms = terms;
        this.agreeYn = agreeYn;
        this.agreeDate = agreeDate;
    }
}