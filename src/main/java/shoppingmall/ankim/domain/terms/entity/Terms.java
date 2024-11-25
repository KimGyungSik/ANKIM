package shoppingmall.ankim.domain.terms.entity;

import jakarta.persistence.*;
import lombok.*;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.global.audit.BaseEntity;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "terms")
public class Terms extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parents_no") // 부모 약관의 FK로 참조
    private Terms parentTerms;

    @OneToMany(mappedBy = "parentTerms", cascade = ALL, orphanRemoval = true)
    private List<Terms> subTerms = new ArrayList<>(); // 부모약관이 가지고 있는 자식 약관

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TermsCategory category; // 약관 유형

    @Column(length = 200, nullable = false)
    private String name; // 약관명

    @Column(columnDefinition = "TEXT")
    private String contents; // 약관 내용

    @Column(name = "terms_yn", length = 1, nullable = false)
    private String termsYn; // 필수 동의 여부

    @Column(name = "version", length = 10, nullable = false)
    private String termsVersion; // 약관 버전

    @Column(nullable = false)
    private Integer level; // 약관 레벨

    @Column(name = "active_yn", length = 1, nullable = false)
    private String activeYn; // 활성화 상태

    @Builder
    public Terms(Terms parentTerms, TermsCategory category, String name, String termsYn, String contents, String termsVersion, Integer level, String activeYn) {
        this.parentTerms = parentTerms;
        this.category = category;
        this.name = name;
        this.termsYn = termsYn;
        this.contents = contents;
        this.termsVersion = termsVersion;
        this.level = level;
        this.activeYn = activeYn;
    }
}