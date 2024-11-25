package shoppingmall.ankim.domain.terms.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shoppingmall.ankim.domain.member.entity.Member;
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

    @ManyToOne
    @JoinColumn(name = "mem_no", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "terms_no", nullable = false)
    private Terms terms;

    @Column(name = "terms_yn", length = 1, nullable = false)
    private String termsYn = "N"; // 동의 여부

    @Column(name = "agree_date", nullable = false)
    private LocalDateTime agreeDate = LocalDateTime.now(); // 동의 일자


}