package shoppingmall.ankim.domain.payment.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCancelRequest {
    private String paymentKey;
    private String cancelReason;
}
