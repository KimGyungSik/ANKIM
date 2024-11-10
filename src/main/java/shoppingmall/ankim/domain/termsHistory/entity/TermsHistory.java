package shoppingmall.ankim.domain.termsHistory.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.terms.entity.Terms;
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

    @Column(name = "terms_yn", length = 1, nullable = false)
    private String termsYn; // 동의 여부

    @Column(name = "terms_ver", length = 10, nullable = false)
    private String termsVersion; // 약관 버전

    @Column(name = "agree_date", nullable = false)
    private LocalDateTime agreeDate = LocalDateTime.now(); // 동의 일자


}