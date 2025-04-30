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
import shoppingmall.ankim.domain.delivery.entity.DeliveryStatus;
import shoppingmall.ankim.domain.delivery.exception.DeliveryNotFoundException;
import shoppingmall.ankim.domain.delivery.repository.DeliveryRepository;
import shoppingmall.ankim.domain.order.events.OrderCanceledEvent;

import static shoppingmall.ankim.domain.delivery.entity.DeliveryStatus.*;
import static shoppingmall.ankim.global.exception.ErrorCode.DELIVERY_NOT_FOUND;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryStatusCancelEventHandler {

    private final DeliveryRepository deliveryRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(OrderCanceledEvent event) {
        Delivery delivery = deliveryRepository.findByOrder(event.getOrderId())
                .orElseThrow(() -> new DeliveryNotFoundException(DELIVERY_NOT_FOUND));
        log.info("[DeliveryStatusCancelEventHandler] 배송 취소 이벤트");
        delivery.setStatus(CANCELED);
    }
}

