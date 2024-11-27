package shoppingmall.ankim.domain.payment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.global.audit.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Payment")
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @OneToOne(fetch = FetchType.LAZY)
    @Column(name = "ord_no", nullable = false)
    private Order order;

    private String type; // 결제 유형

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice; // 총 결제 금액

    private String name; // 결제명

    @Column(name = "pay_key")
    private String payKey; // 결제키

    @Column(name = "pay_date")
    private LocalDateTime payDate; // 결제 요청 일시
}

