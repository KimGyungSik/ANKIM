package shoppingmall.ankim.domain.delivery.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import shoppingmall.ankim.domain.delivery.entity.Delivery;
import shoppingmall.ankim.domain.delivery.service.DeliveryService;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.order.repository.OrderRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryCreateRequestedEventHandler {

    private final DeliveryService deliveryService;
    private final OrderRepository orderRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async
    public void handle(DeliveryCreateRequestedEvent event) {
        Order order = event.getOrder();
        try {
            log.info("[DeliveryCreateRequestedEventHandler] 배송 생성 시작");

            Delivery delivery = deliveryService.createDelivery(
                    event.getDeliveryRequest(),
                    event.getAddressRequest(),
                    order.getMember().getLoginId()
            );
            order.setDelivery(delivery);
            orderRepository.save(order);

        } catch (Exception e) {
            log.error("[DeliveryCreateRequestedEventHandler] 배송 생성 실패", e);
        }
    }
}
