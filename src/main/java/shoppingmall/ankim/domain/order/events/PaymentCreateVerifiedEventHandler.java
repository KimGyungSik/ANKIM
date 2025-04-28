package shoppingmall.ankim.domain.order.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import shoppingmall.ankim.domain.delivery.events.DeliveryCreateRequestedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentCreateVerifiedEventHandler {

    private final ApplicationEventPublisher eventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(PaymentCreateVerifiedEvent event) {
        log.info("[PaymentCreateVerifiedEvent] 주문 검증 완료, 배송 생성 이벤트 발행");

        eventPublisher.publishEvent(new DeliveryCreateRequestedEvent(
                event.getOrder(),
                event.getDeliveryRequest(),
                event.getAddressRequest(),
                event.getPaymentRequest()
        ));
    }
}
