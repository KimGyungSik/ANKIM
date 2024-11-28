package shoppingmall.ankim.domain.payment.controller.request;

import lombok.Data;

@Data
public class PaymentSuccessRequest {
    private String paymentKey;
    private String orderId;
    private Integer amount;
}
