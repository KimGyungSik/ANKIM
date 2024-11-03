package shoppingmall.ankim.domain.terms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "terms_history")
public class TermsHistory {

    @EmbeddedId
    private TermsHistoryId id;

    @ManyToOne
    @JoinColumn(name = "code", nullable = false)
    private Terms terms; // 약관 코드 (FK)

    @Column(name = "terms_yn", length = 1, nullable = false)
    private String termsYn; // 동의 여부

    @Column(name = "agree_date", nullable = false)
    private LocalDateTime agreeDate; // 동의 일자

}