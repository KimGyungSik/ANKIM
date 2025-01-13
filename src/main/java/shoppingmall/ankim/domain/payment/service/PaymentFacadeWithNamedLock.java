package shoppingmall.ankim.domain.payment.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import shoppingmall.ankim.domain.address.service.request.MemberAddressCreateServiceRequest;
import shoppingmall.ankim.domain.cart.entity.Cart;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.cart.exception.CartNotFoundException;
import shoppingmall.ankim.domain.cart.repository.CartRepository;
import shoppingmall.ankim.domain.delivery.entity.Delivery;
import shoppingmall.ankim.domain.delivery.service.DeliveryService;
import shoppingmall.ankim.domain.delivery.service.request.DeliveryCreateServiceRequest;
import shoppingmall.ankim.domain.item.service.ItemService;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.order.exception.OrderNotFoundException;
import shoppingmall.ankim.domain.order.repository.OrderRepository;
import shoppingmall.ankim.domain.orderItem.entity.OrderItem;
import shoppingmall.ankim.domain.payment.controller.port.PaymentService;
import shoppingmall.ankim.domain.payment.dto.PaymentCancelResponse;
import shoppingmall.ankim.domain.payment.dto.PaymentFailResponse;
import shoppingmall.ankim.domain.payment.dto.PaymentResponse;
import shoppingmall.ankim.domain.payment.dto.PaymentSuccessResponse;
import shoppingmall.ankim.domain.payment.entity.Payment;
import shoppingmall.ankim.domain.payment.exception.AlreadyApprovedException;
import shoppingmall.ankim.domain.payment.exception.PaymentNotFoundException;
import shoppingmall.ankim.domain.payment.repository.PaymentRepository;
import shoppingmall.ankim.domain.payment.service.request.PaymentCreateServiceRequest;
import shoppingmall.ankim.global.config.lock.LockHandler;
import shoppingmall.ankim.global.config.lock.NamedLock;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static shoppingmall.ankim.domain.orderItem.entity.OrderStatus.PENDING_PAYMENT;
import static shoppingmall.ankim.global.exception.ErrorCode.*;

// TODO Facade패턴 구현하면서 결제 상태 변경 내역 엔티티도 고려해봐야함
// TODO 결제를 하고 나서 결제를 취소했을 때 내역이 있어야 하나?
// TODO 배송 상태 이력 테이블도 고려해야함 -> 배송 상태가 바뀔 때마다 이력이 쌓이도록
// TODO 트랜잭션 경계에 맞춰서 결제 처리 실패시 재고 차감, 복구도 같이 롤백 시켜야하는 문제도 고려해봐야함
// TODO 결제를 요청하고 나서 재고차감을 했는데 결제 요청 API에서 에러 발생 시 재고 감소 롤백 말하는거임

// FIXME 비동기처리는 즉 @Async는 @Transactional과 동일하게 AOP방식으로 동작하므로 다른 서비스 클래스로 분리시켜줘야함
@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PaymentFacadeWithNamedLock {
    private final LockHandler lockHandler;
    private final ItemService itemService;
    private final DeliveryService deliveryService;
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository;

    // 클라이언트 결제 요청처리 & 재고 감소 & 배송지 저장
    public PaymentResponse createPaymentWithNamedLock(PaymentCreateServiceRequest request,
                                              DeliveryCreateServiceRequest deliveryRequest,
                                              MemberAddressCreateServiceRequest addressRequest) {
        // Order 조회 (fetch join으로 Member 로딩)
        Order order = orderRepository.findByOrderNameWithMemberAndOrderItemsAndItem(request.getOrderName())
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));

        // 결제 대기중 상태가 아니라면 이미 승인된 결제이므로 예외 발생
        if (order.getOrderStatus() != PENDING_PAYMENT) {
            throw new AlreadyApprovedException(ALREADY_APPROVED);
        }

        // 배송지 생성
        Delivery delivery = deliveryService.createDelivery(deliveryRequest, addressRequest, order.getMember().getLoginId());
        order.setDelivery(delivery);

        // 재고 차감
        reduceStock(order);

        // 결제 요청 처리
        return paymentService.requestTossPayment(request);
    }

//    @Async
//    public void createDelivery(DeliveryCreateServiceRequest deliveryRequest, MemberAddressCreateServiceRequest addressRequest, Order order) {
//        Delivery delivery = deliveryService.createDelivery(deliveryRequest, addressRequest, order.getMember().getLoginId());
//        order.setDelivery(delivery);
//    }

    // 결제 성공 시 처리 & 주문 상태 (결제완료) & 장바구니 주문 상품 비활성화 (장바구니 비우기)
    public PaymentSuccessResponse toSuccessRequest(String paymentKey, String orderId, Integer amount) {
        // 주문상태를 결제완료로 수정
        Order order = orderRepository.findByOrderIdWithMemberAndOrderItems(orderId)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
        order.successOrder();

        // 주문 상품은 장바구니에서 비우기
        // 주문 상품과 장바구니 상품 매핑하여 비활성화
        deactivateCartItemsMappedToOrder(order);
        return paymentService.tossPaymentSuccess(paymentKey,orderId,amount);
    }

    @Async
    public void deactivateCartItemsMappedToOrder(Order order) {
        Cart cart = cartRepository.findByMemberAndActiveYn(order.getMember(), "Y")
                .orElseThrow(() -> new CartNotFoundException(CART_NOT_FOUND));

        List<OrderItem> orderItems = order.getOrderItems();
        List<CartItem> cartItems = cart.getCartItems();

        for (OrderItem orderItem : orderItems) {
            cartItems.stream()
                    .filter(cartItem -> isMatchingCartAndOrder(cartItem, orderItem)) // 일치 여부 확인
                    .forEach(CartItem::deactivate); // 비활성화
        }
    }

    // 결제 실패 시 처리 & 재고 복구 & 주문 상태 (결제실패) & 배송지 삭제
    public PaymentFailResponse toFailRequest(String code, String message, String orderId) {
        // 주문 상태를 결제실패로 수정 & 배송지 삭제
        Order order = orderRepository.findByOrderIdWithMemberAndDeliveryAndOrderItems(orderId)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
        order.failOrderWithOutDelivery();
        // 재고 복구
        restoreStock(order);

        return paymentService.tossPaymentFail(code,message,orderId);
    }
    // 결제 취소 시 처리 & 재고 복구 & 주문 상태 (결제취소) & 배송 상태 (배송 취소)
    public PaymentCancelResponse toCancelRequest(String paymentKey, String cancelReason) {
        Payment payment = paymentRepository.findByPayKeyWithOrder(paymentKey).orElseThrow(() -> new PaymentNotFoundException(PAYMENT_NOT_FOUND));

        Order order = orderRepository.findByOrderIdWithMemberAndDeliveryAndOrderItems(payment.getOrder().getOrdNo())
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));;
        // 단, 결제 취소 시 결제상태가 결제완료, 배송 상태가 배송 준비 상태일때만 가능
        order.cancelOrder(); // 주문 및 배송 취소 처리

        // 재고 복구
        restoreStock(order);

        return paymentService.cancelPayment(paymentKey,cancelReason);
    }

    private void reduceStock(Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            Long itemNo = orderItem.getItem().getNo();
            Integer quantity = orderItem.getQty();
            log.debug("Reducing stock for itemNo: {}, quantity: {}", itemNo, quantity);

            // 재고 감소 로직
            itemService.reduceStock(itemNo, quantity);
        }
    }

    private void restoreStock(Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            Long itemNo = orderItem.getItem().getNo();
            Integer quantity = orderItem.getQty();
            log.debug("Restore stock for itemNo: {}, quantity: {}", itemNo, quantity);
//            String key = String.valueOf(itemNo);
//            try {
                // 아이템별 락
//                lockHandler.lock(key);

                // 아이템 단위로 재고 감소
                itemService.restoreStock(itemNo, quantity);
//            } finally {
//                // 락 해제
//                lockHandler.unlock(key);
//            }
        }
    }
    private boolean isMatchingCartAndOrder(CartItem cartItem, OrderItem orderItem) {
        return cartItem.getItem().getNo().equals(orderItem.getItem().getNo()) // 품목 번호 일치
                && cartItem.getQty().equals(orderItem.getQty());        // 수량 일치
    }
}
