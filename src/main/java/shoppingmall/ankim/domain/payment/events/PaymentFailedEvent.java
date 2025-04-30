package shoppingmall.ankim.domain.payment.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentFailedEvent {
    private final String orderId;
}


