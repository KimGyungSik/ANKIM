package shoppingmall.ankim.domain.item.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import shoppingmall.ankim.domain.item.service.ItemService;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.order.exception.OrderNotFoundException;
import shoppingmall.ankim.domain.order.repository.OrderRepository;
import shoppingmall.ankim.domain.payment.events.PaymentFailedEvent;

import static shoppingmall.ankim.global.exception.ErrorCode.ORDER_NOT_FOUND;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockRestoreEventHandler {

    private final OrderRepository orderRepository;
    private final ItemService itemService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(PaymentFailedEvent event) {
        String orderId = event.getOrderId();
        Order order = orderRepository.findByOrderIdWithMemberAndOrderItems(orderId)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));

        log.info("[StockRestoreEventHandler] 결제 실패 → 재고 복구 시작");
        order.getOrderItems().forEach(orderItem ->
                itemService.restoreStock(orderItem.getItem().getNo(), orderItem.getQty())
        );
    }
}

