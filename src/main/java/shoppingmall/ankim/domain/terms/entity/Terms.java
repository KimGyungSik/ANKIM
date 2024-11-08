package shoppingmall.ankim.domain.terms.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shoppingmall.ankim.global.audit.BaseEntity;

import java.util.List;

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

    @Column(name = "code", length = 7)
    private String code; // 구분-번호

    @Column(length = 200, nullable = false)
    private String name; // 약관명

    @Column(columnDefinition = "TEXT")
    private String contents; // 약관 내용

    @Column(name = "terms_yn", length = 1, nullable = false)
    private String termsYn = "N"; // 필수 동의 여부

    @Column(nullable = false)
    private Integer level; // 약관 레벨

    @Column(name = "active_yn", length = 1, nullable = false)
    private String activeYn = "Y"; // 활성화 상태

}