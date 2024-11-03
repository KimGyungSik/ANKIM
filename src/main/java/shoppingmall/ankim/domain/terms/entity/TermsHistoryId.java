package shoppingmall.ankim.domain.terms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serializable;

public class TermsHistoryId implements Serializable {
    @Column(name = "mem_no", nullable = false)
    private Long memNo; // 회원 번호 (외래 키)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no; // 자동 증가 ID
}
