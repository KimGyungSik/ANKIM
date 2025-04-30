package shoppingmall.ankim.domain.cart.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import shoppingmall.ankim.domain.cart.entity.Cart;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.cart.exception.CartNotFoundException;
import shoppingmall.ankim.domain.cart.repository.CartRepository;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.order.exception.OrderNotFoundException;
import shoppingmall.ankim.domain.order.repository.OrderRepository;
import shoppingmall.ankim.domain.orderItem.entity.OrderItem;
import shoppingmall.ankim.domain.payment.events.PaymentSuccessProcessedEvent;

import static shoppingmall.ankim.global.exception.ErrorCode.CART_NOT_FOUND;
import static shoppingmall.ankim.global.exception.ErrorCode.ORDER_NOT_FOUND;

@Component
@RequiredArgsConstructor
@Slf4j
public class CartItemDeactivateEventHandler {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async
    public void handle(PaymentSuccessProcessedEvent event) {
        Order order = orderRepository.findByOrderIdWithMemberAndOrderItems(event.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));

        Cart cart = cartRepository.findByMemberAndActiveYn(order.getMember(), "Y")
                .orElseThrow(() -> new CartNotFoundException(CART_NOT_FOUND));

        order.getOrderItems().forEach(orderItem ->
                cart.getCartItems().stream()
                        .filter(cartItem -> isMatchingCartAndOrder(cartItem, orderItem))
                        .forEach(CartItem::deactivate)
        );

        log.info("[CartItemDeactivateEventHandler] 장바구니 항목 비활성화 완료");
    }

    private boolean isMatchingCartAndOrder(CartItem cartItem, OrderItem orderItem) {
        return cartItem.getItem().getNo().equals(orderItem.getItem().getNo()) // 품목 번호 일치
                && cartItem.getQty().equals(orderItem.getQty());        // 수량 일치
    }
}

