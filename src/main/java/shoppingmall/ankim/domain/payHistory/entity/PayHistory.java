package shoppingmall.ankim.domain.payHistory.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PayHistory")
public class PayHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(name = "pay_no", nullable = false)
    private Long payNo;

    private String status;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "changed_at")
    private LocalDateTime changedAt;
}