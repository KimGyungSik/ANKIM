package shoppingmall.ankim.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.payment.entity.PayType;
import shoppingmall.ankim.domain.payment.entity.Payment;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private PayType payType; // 결제 타입 - 카드/현금/포인트
    private Integer amount; // 총 결제 금액
    private Long orderId; // 주문 아이디
    private String orderName; // 주문 코드
    private String customerEmail; // 고객 이메일
    private String customerName; // 고객 이름
    private String successUrl; // 성공 시 리다이렉트 될 URL
    private String failUrl; // 실패 시 리다이렉트 될 URL

    private String failReason; // 실패 이유
    private boolean cancelYN; // 취소 YN
    private String cancelReason; // 취소 이유
    private LocalDateTime createdAt; // 결제가 이루어진 시간


    public static PaymentResponse of(Payment payment) {
        return PaymentResponse.builder()
                .payType(payment.getType())
                .amount(payment.getTotalPrice())
                .orderId(payment.getOrder().getOrdNo())
                .orderName(payment.getOrder().getOrdCode())
                .customerEmail(payment.getOrder().getMember().getLoginId())
                .customerName(payment.getOrder().getMember().getName())
                .failReason(payment.getFailReason())
                .cancelReason(payment.getCancelReason())
                .cancelYN(payment.isCancelYN())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
