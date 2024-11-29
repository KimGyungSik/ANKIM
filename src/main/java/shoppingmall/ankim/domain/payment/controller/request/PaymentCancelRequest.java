package shoppingmall.ankim.domain.payment.controller.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentCancelRequest {
    private String paymentKey;
    private String cancelReason;
}
