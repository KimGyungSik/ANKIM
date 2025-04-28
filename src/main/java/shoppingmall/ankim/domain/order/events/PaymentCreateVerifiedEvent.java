package shoppingmall.ankim.domain.order.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shoppingmall.ankim.domain.address.service.request.MemberAddressCreateServiceRequest;
import shoppingmall.ankim.domain.delivery.service.request.DeliveryCreateServiceRequest;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.payment.service.request.PaymentCreateServiceRequest;

@Getter
@AllArgsConstructor
public class PaymentCreateVerifiedEvent {
    private final Order order;
    private final PaymentCreateServiceRequest paymentRequest;
    private final DeliveryCreateServiceRequest deliveryRequest;
    private final MemberAddressCreateServiceRequest addressRequest;
}
