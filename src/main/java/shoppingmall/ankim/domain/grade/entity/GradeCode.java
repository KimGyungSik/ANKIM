package shoppingmall.ankim.domain.grade.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shoppingmall.ankim.global.audit.BaseEntity;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GradeCode extends BaseEntity {

    @Id
    private Long no; // 등급 번호 (50 / 100 / 150 / 200)

    @Column(name = "name", length = 20)
    private String name = "Green"; // 등급 이름

    @Column(name = "min_ord_price")
    private Integer minOrderPrice = 0; // 최소 주문 금액

    @Column(name = "max_ord_price")
    private Integer maxOrderPrice = 10; // 최대 주문 금액

    @Column(name = "benefit_desc", length = 255)
    private String benefitDescription; // 혜택 설명

    @Column(name = "active_yn", columnDefinition = "CHAR(1) DEFAULT 'Y'")
    private String activeYn = "Y"; // 활성화 여부
}
