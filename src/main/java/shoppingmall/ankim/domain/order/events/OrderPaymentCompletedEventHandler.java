package shoppingmall.ankim.domain.order.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.order.exception.OrderNotFoundException;
import shoppingmall.ankim.domain.order.repository.OrderRepository;
import shoppingmall.ankim.domain.payment.events.PaymentSuccessProcessedEvent;

import static shoppingmall.ankim.global.exception.ErrorCode.ORDER_NOT_FOUND;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderPaymentCompletedEventHandler {

    private final OrderRepository orderRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async
    public void handle(PaymentSuccessProcessedEvent event) {
        Order order = orderRepository.findByOrderIdWithMemberAndOrderItems(event.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));

        order.successOrder();
        log.info("[OrderPaymentCompletedEventHandler] 주문 상태 '결제 완료'로 변경 완료");
    }
}

