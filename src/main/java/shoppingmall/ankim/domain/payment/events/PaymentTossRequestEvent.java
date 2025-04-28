package shoppingmall.ankim.domain.payment.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.payment.service.request.PaymentCreateServiceRequest;

@Getter
@AllArgsConstructor
public class PaymentTossRequestEvent {
    private final Order order;
    private final PaymentCreateServiceRequest paymentRequest;
}
