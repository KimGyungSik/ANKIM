package shoppingmall.ankim.domain.delivery.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import shoppingmall.ankim.domain.delivery.entity.Delivery;
import shoppingmall.ankim.domain.delivery.service.DeliveryService;
import shoppingmall.ankim.domain.item.events.StockReduceRequestedEvent;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.order.repository.OrderRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryCreateRequestedEventHandler {

    private final DeliveryService deliveryService;
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(DeliveryCreateRequestedEvent event) {
        Order order = event.getOrder();
        System.out.println(order);
        try {
            log.info("[DeliveryCreateRequestedEventHandler] 배송 생성 시작");

            Delivery delivery = deliveryService.createDelivery(
                    event.getDeliveryRequest(),
                    event.getAddressRequest(),
                    order.getMember().getLoginId()
            );
            order.setDelivery(delivery);
            orderRepository.save(order);

            // 배송 생성 성공 → 재고 차감 이벤트 발행
            eventPublisher.publishEvent(new StockReduceRequestedEvent(
                    order,
                    event.getPaymentRequest()
            ));

        } catch (Exception e) {
            log.error("[DeliveryCreateRequestedEventHandler] 배송 생성 실패, 주문 상태 FAILED_PAYMENT로 변경", e);

            // 실패 시 주문 상태를 결제 실패로 변경
            order.failOrderWithOutDelivery();

            // 이벤트 흐름 중단 (이벤트 체인 끊기)
            throw new RuntimeException("배송 생성 실패로 이벤트 체인 중단");
        }
    }
}
