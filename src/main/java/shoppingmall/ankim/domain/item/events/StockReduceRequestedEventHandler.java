package shoppingmall.ankim.domain.item.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import shoppingmall.ankim.domain.item.service.ItemService;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.payment.events.PaymentTossRequestEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockReduceRequestedEventHandler {

    private final ItemService itemService;
    private final ApplicationEventPublisher eventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(StockReduceRequestedEvent event) {
        Order order = event.getOrder();

        try {
            log.info("[StockReduceRequestedEventHandler] 재고 차감 시작");

            order.getOrderItems().forEach(orderItem -> {
                itemService.reduceStock(orderItem.getItem().getNo(), orderItem.getQty());
            });

            // 재고 차감 성공 → 결제 요청 이벤트 발행
            eventPublisher.publishEvent(new PaymentTossRequestEvent(
                    order,
                    event.getPaymentRequest()
            ));

        } catch (Exception e) {
            log.error("[StockReduceRequestedEventHandler] 재고 차감 실패, 주문 상태 FAILED_PAYMENT로 변경", e);

            // 주문 상태 결제 실패로 변경
            order.failOrderWithOutDelivery();

            // ✅ 재고 복구 이벤트 발행
            eventPublisher.publishEvent(new StockRestoreRequestedEvent(order));

            throw new RuntimeException("재고 차감 실패로 이벤트 체인 중단");
        }
    }
}
