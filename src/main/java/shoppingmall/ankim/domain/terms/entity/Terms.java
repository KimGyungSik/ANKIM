package shoppingmall.ankim.domain.terms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter @Setter
@Table(name = "terms")
public class Terms {

    @Id
    @Column(length = 7)
    private String code; // 구분-번호

    @Column(name = "prents_code", length = 7, nullable = false)
    private String prentsCode; // 상위 약관 코드

    @Column(length = 200, nullable = false)
    private String name; // 약관명

    @Column(columnDefinition = "TEXT")
    private String contents; // 약관 내용

    @Column(name = "terms_yn", length = 1, nullable = false)
    private String termsYn; // 필수 동의 여부

    @Column(nullable = false)
    private Integer level; // 약관 레벨

    @Column(name = "active_yn", length = 1, nullable = false)
    private String activeYn = "Y"; // 활성화 상태

    @OneToMany(mappedBy = "terms")
    private List<TermsHistory> termsHistories; // 연관된 약관 동의 이력

}