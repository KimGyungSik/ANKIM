package shoppingmall.ankim.domain.order.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.order.exception.OrderNotFoundException;
import shoppingmall.ankim.domain.order.repository.OrderRepository;
import shoppingmall.ankim.domain.payment.events.PaymentFailedEvent;

import static shoppingmall.ankim.global.exception.ErrorCode.ORDER_NOT_FOUND;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderPaymentFailedEventHandler {

    private final OrderRepository orderRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(PaymentFailedEvent event) {
        String orderId = event.getOrderId();
        Order order = orderRepository.findByOrdNo(orderId)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));

        log.info("[OrderPaymentFailedEventHandler] 주문 상태 FAILED_PAYMENT 및 배송지 삭제 시작");
        order.failOrderWithOutDelivery();
        orderRepository.save(order);
    }
}

