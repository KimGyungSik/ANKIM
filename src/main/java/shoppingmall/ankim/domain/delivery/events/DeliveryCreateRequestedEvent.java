package shoppingmall.ankim.domain.delivery.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shoppingmall.ankim.domain.address.service.request.MemberAddressCreateServiceRequest;
import shoppingmall.ankim.domain.delivery.service.request.DeliveryCreateServiceRequest;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.payment.service.request.PaymentCreateServiceRequest;

@Getter
@AllArgsConstructor
public class DeliveryCreateRequestedEvent {
    private final Order order;
    private final DeliveryCreateServiceRequest deliveryRequest;
    private final MemberAddressCreateServiceRequest addressRequest;
    private final PaymentCreateServiceRequest paymentRequest;
}