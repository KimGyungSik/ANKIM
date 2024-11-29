package shoppingmall.ankim.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.payment.entity.PayType;
import shoppingmall.ankim.domain.payment.entity.Payment;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentHistoryResponse {
    private Long paymentId; // 결제 ID
    private Integer totalPrice; // 결제 금액
    private String orderName; // 주문 코드

    private String type; // 결제 유형

    private boolean isPaySuccessYN; // 결제 성공여부

    private LocalDateTime createdAt; // 결제 요청일시

    public static PaymentHistoryResponse of(Payment payment) {
        return PaymentHistoryResponse.builder()
                .paymentId(payment.getNo())
                .totalPrice(payment.getTotalPrice())
                .orderName(payment.getOrder().getOrdCode())
                .type(payment.getType().getDescription())
                .isPaySuccessYN(payment.isPaySuccessYN())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
