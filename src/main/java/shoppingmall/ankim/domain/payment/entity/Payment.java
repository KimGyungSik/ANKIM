package shoppingmall.ankim.domain.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.payment.exception.InvalidPaymentKeyException;
import shoppingmall.ankim.global.audit.BaseEntity;

import static shoppingmall.ankim.global.exception.ErrorCode.INVALID_PAYMENT_KEY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Payment")
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @OneToOne(fetch = FetchType.LAZY)
    private Order order;

    @Column(name = "pay_key")
    private String payKey; // 결제키

    @Enumerated(EnumType.STRING)
    private PayType type; // 결제 유형 - 카드/현금/포인트

    @Column(name = "total_price")
    private Integer totalPrice; // 총 결제 금액

    private boolean paySuccessYN;

    @Column
    private String failReason; // 결제 실패 이유

    @Column
    private boolean cancelYN; // 결제 취소 여부

    @Setter
    @Column
    private String cancelReason; // 결제 취소 이유

    @Builder
    private Payment(Order order, String payKey, PayType type, Integer totalPrice, boolean paySuccessYN, String failReason, boolean cancelYN, String cancelReason) {
        this.order = order;
        this.payKey = payKey;
        this.type = type;
        this.totalPrice = totalPrice;
        this.paySuccessYN = paySuccessYN;
        this.failReason = failReason;
        this.cancelYN = cancelYN;
        this.cancelReason = cancelReason;
    }

    public static Payment create(Order order,PayType type,Integer totalPrice) {
        return Payment.builder()
                .order(order)
                .type(type)
                .totalPrice(totalPrice)
                .paySuccessYN(false)
                .build();
    }

    public void setPaymentKey(String paymentKey, boolean paySuccessYN) {
        if(paymentKey == null) {
            throw new InvalidPaymentKeyException(INVALID_PAYMENT_KEY);
        }
        this.payKey = paymentKey;
        this.paySuccessYN = paySuccessYN;
    }

    public void setFailReason(String message, boolean paySuccessYN) {
        this.failReason = message;
        this.paySuccessYN = paySuccessYN;
    }

    public void setPaymentCancel(String cancelReason, boolean cancelYN) {
        this.cancelReason = cancelReason;
        this.cancelYN = cancelYN;
    }
}

