package shoppingmall.ankim.domain.payment.service;

import org.springframework.context.ApplicationEventPublisher;
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
import shoppingmall.ankim.domain.delivery.dto.DeliveryResponse;
import shoppingmall.ankim.domain.delivery.entity.Delivery;
import shoppingmall.ankim.domain.delivery.service.DeliveryService;
import shoppingmall.ankim.domain.delivery.service.request.DeliveryCreateServiceRequest;
import shoppingmall.ankim.domain.item.service.ItemService;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.order.events.PaymentCreateVerifiedEvent;
import shoppingmall.ankim.domain.order.exception.OrderNotFoundException;
import shoppingmall.ankim.domain.order.repository.OrderRepository;
import shoppingmall.ankim.domain.orderItem.entity.OrderItem;
import shoppingmall.ankim.domain.orderItem.entity.OrderStatus;
import shoppingmall.ankim.domain.payment.controller.port.PaymentService;
import shoppingmall.ankim.domain.payment.dto.*;
import shoppingmall.ankim.domain.payment.entity.Payment;
import shoppingmall.ankim.domain.payment.exception.AlreadyApprovedException;
import shoppingmall.ankim.domain.payment.exception.InvalidOrderStatusException;
import shoppingmall.ankim.domain.payment.exception.PaymentNotFoundException;
import shoppingmall.ankim.domain.payment.repository.PaymentRepository;
import shoppingmall.ankim.domain.payment.service.request.PaymentCreateServiceRequest;
import shoppingmall.ankim.global.config.lock.LockHandler;
import shoppingmall.ankim.global.config.lock.NamedLock;
import shoppingmall.ankim.global.exception.ErrorCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static shoppingmall.ankim.domain.orderItem.entity.OrderStatus.PENDING_PAYMENT;
import static shoppingmall.ankim.global.exception.ErrorCode.*;

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
    private final ApplicationEventPublisher eventPublisher;

    // 클라이언트 결제 요청처리 & 재고 감소 & 배송지 저장
    @Transactional
    public void createPaymentWithNamedLock(PaymentCreateServiceRequest request,
                                                      DeliveryCreateServiceRequest deliveryRequest,
                                                      MemberAddressCreateServiceRequest addressRequest) {
        // 1. 주문 조회
        Order order = orderRepository.findByOrderNameWithMemberAndOrderItemsAndItem(request.getOrderName())
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));

        // 2. 결제 대기 상태 확인
        if (order.getOrderStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new AlreadyApprovedException(ALREADY_APPROVED);
        }

        // 3. 주문 검증 완료 시 이벤트 발행
        eventPublisher.publishEvent(new PaymentCreateVerifiedEvent(
                order,
                request,
                deliveryRequest,
                addressRequest
        ));
    }


    // 결제 성공 시 처리 & 주문 상태 (결제완료) & 장바구니 주문 상품 비활성화 (장바구니 비우기)
    // FIXME 결제 요청시에 배송지 생성 & 재고차감이 아닌 결제 성공 이전에 해야할듯
    @Transactional
    public PaymentSuccessResponse toSuccessRequest(String paymentKey, String orderId, Integer amount) {
        log.info("[toSuccessRequest] 결제 성공 콜백 처리 시작: orderId={}, paymentKey={}", orderId, paymentKey);

        // 1. 주문 조회
        Order order = orderRepository.findByOrderIdWithMemberAndDeliveryAndOrderItems(orderId)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));

        // 2. 주문 상태 체크
        if (order.getOrderStatus() != OrderStatus.PENDING_PAYMENT) {
            log.error("[toSuccessRequest] 주문 상태가 결제 가능 상태가 아님. 현재 상태: {}", order.getOrderStatus());

            // Toss 결제 취소 요청
            paymentService.cancelPayment(paymentKey, "상품 준비 실패로 인한 자동 환불");

            throw new InvalidOrderStatusException(INVALID_ORDER_STATUS);
        }

        // 3. 결제 성공 처리
        PaymentSuccessResponse tossPaymentSuccess = paymentService.tossPaymentSuccess(paymentKey, orderId, amount);

        // 4. 주문 상태 업데이트
        order.successOrder(); // 주문 상태를 PAID로 변경

        // 5. 주문 상품 장바구니 비우기 (CartItem 비활성화)
        deactivateCartItemsMappedToOrder(order);

        // 6. 추가 응답 데이터 세팅
        tossPaymentSuccess.setPaymentSuccessInfoResponse(PaymentSuccessInfoResponse.builder()
                .totalShipFee(order.getTotalShipFee())
                .deliveryResponse(DeliveryResponse.of(order.getDelivery()))
                .build());

        log.info("[toSuccessRequest] 결제 성공 처리 완료: orderId={}", orderId);

        return tossPaymentSuccess;
    }


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
        PaymentFailResponse response = paymentService.tossPaymentFail(code, message, orderId);
        if(response!=null) {
            // 주문 상태를 결제실패로 수정 & 배송지 삭제
            Order order = orderRepository.findByOrderIdWithMemberAndDeliveryAndOrderItems(orderId)
                    .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
            order.failOrderWithOutDelivery();
            response.setOrderName(order.getOrdCode());
            // 재고 복구
            restoreStock(order);
        }
        return response;
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

            itemService.restoreStock(itemNo, quantity);
        }
    }
    private boolean isMatchingCartAndOrder(CartItem cartItem, OrderItem orderItem) {
        return cartItem.getItem().getNo().equals(orderItem.getItem().getNo()) // 품목 번호 일치
                && cartItem.getQty().equals(orderItem.getQty());        // 수량 일치
    }
}
