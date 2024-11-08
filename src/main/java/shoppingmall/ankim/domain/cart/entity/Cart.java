package shoppingmall.ankim.domain.cart.entity;

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
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no; // 자동 증가 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mem_no", nullable = false)
    private Member member; // 회원 번호 (외래 키)

    @Column(name = "reg_date", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime regDate; // 등록 날짜

    @Column(name = "active_yn", length = 1, nullable = false)
    private String activeYn = "Y"; // 활성화 상태
}