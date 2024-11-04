package shoppingmall.ankim.domain.payment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(name = "ord_no", nullable = false)
    private Long ordNo;

    private String type;

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;

    private String name;

    @Column(name = "pay_key")
    private String payKey;

    @Column(name = "pay_date")
    private LocalDateTime payDate;
}

