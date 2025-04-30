package shoppingmall.ankim.domain.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.address.service.request.MemberAddressCreateServiceRequest;
import shoppingmall.ankim.domain.cart.entity.Cart;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.cart.exception.CartNotFoundException;
import shoppingmall.ankim.domain.cart.repository.CartRepository;
import shoppingmall.ankim.domain.delivery.entity.Delivery;
import shoppingmall.ankim.domain.delivery.service.DeliveryService;
import shoppingmall.ankim.domain.delivery.service.request.DeliveryCreateServiceRequest;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.item.exception.InvalidStockQuantityException;
import shoppingmall.ankim.domain.item.exception.ItemNotFoundException;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
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

import java.util.List;

import static shoppingmall.ankim.domain.orderItem.entity.OrderStatus.PENDING_PAYMENT;
import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PaymentFacadeWithPessimisticLock {
    private final ItemRepository itemRepository;
    private final DeliveryService deliveryService;
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository;

    // 클라이언트 결제 요청처리 & 재고 감소 & 배송지 저장
//    public PaymentResponse createPaymentWithPessimisticLock(PaymentCreateServiceRequest request,
//                                                         DeliveryCreateServiceRequest deliveryRequest,
//                                                         MemberAddressCreateServiceRequest addressRequest) {
//        // Order 조회 (fetch join으로 Member 로딩)
//        Order order = orderRepository.findByOrderNameWithMemberAndOrderItems(request.getOrderName())
//                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
//
//        // 결제 대기중 상태가 아니라면 이미 승인된 결제이므로 예외 발생
//        if (order.getOrderStatus() != PENDING_PAYMENT) {
//            throw new AlreadyApprovedException(ALREADY_APPROVED);
//        }
//
//        // 배송지 생성
//        Delivery delivery = deliveryService.createDelivery(deliveryRequest, addressRequest, order.getMember().getLoginId());
//        order.setDelivery(delivery);
//
//        // 재고 차감
//        reduceStock(order);
//
//        // 결제 요청 처리
//        return paymentService.requestTossPayment(request);
//    }

    // 결제 성공 시 처리 & 주문 상태 (결제완료) & 장바구니 주문 상품 비활성화 (장바구니 비우기)
    public PaymentSuccessResponse toSuccessRequestWithPessimisticLock(String paymentKey, String orderId, Integer amount) {
        // 주문상태를 결제완료로 수정
        Order order = orderRepository.findByOrderIdWithMemberAndOrderItems(orderId)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
        order.successOrder();

        // 주문 상품은 장바구니에서 비우기
        Cart cart = cartRepository.findByMemberAndActiveYn(order.getMember(), "Y")
                .orElseThrow(() -> new CartNotFoundException(CART_NOT_FOUND));

        // 주문 상품과 장바구니 상품 매핑하여 비활성화
        List<OrderItem> orderItems = order.getOrderItems();
        List<CartItem> cartItems = cart.getCartItems();

        for (OrderItem orderItem : orderItems) {
            cartItems.stream()
                    .filter(cartItem -> isMatchingCartAndOrder(cartItem, orderItem)) // 일치 여부 확인
                    .forEach(CartItem::deactivate); // 비활성화
        }
        return paymentService.tossPaymentSuccess(paymentKey,orderId,amount);
    }

    // 결제 실패 시 처리 & 재고 복구 & 주문 상태 (결제실패) & 배송지 삭제
    public PaymentFailResponse toFailRequestWithPessimisticLock(String code, String message, String orderId) {
        // 주문 상태를 결제실패로 수정 & 배송지 삭제
        Order order = orderRepository.findByOrderIdWithMemberAndDeliveryAndOrderItems(orderId)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
        order.failOrderWithOutDelivery();

        // 재고 복구
        restoreStock(order);

        return paymentService.tossPaymentFail(code,message,orderId);
    }
    // 결제 취소 시 처리 & 재고 복구 & 주문 상태 (결제취소) & 배송 상태 (배송 취소)
    public PaymentCancelResponse toCancelRequestWithPessimisticLock(String paymentKey, String cancelReason) {
        Payment payment = paymentRepository.findByPayKeyWithOrder(paymentKey).orElseThrow(() -> new PaymentNotFoundException(PAYMENT_NOT_FOUND));

        Order order = orderRepository.findByOrderIdWithMemberAndDeliveryAndOrderItems(payment.getOrder().getOrdNo())
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));;
        // 단, 결제 취소 시 배송 상태가 배송 준비 상태일때만 가능
        order.cancelOrder(); // 주문 및 배송 취소 처리

        // 재고 복구
        restoreStock(order);

        return paymentService.cancelPayment(paymentKey,cancelReason);
    }

    private void reduceStock(Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            Item item = itemRepository.findByIdWithPessimisticLock(orderItem.getItem().getNo())
                    .orElseThrow(()-> new ItemNotFoundException(ITEM_NOT_FOUND));
            item.deductQuantity(orderItem.getQty());
        }
    }
    private void restoreStock(Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            Item item = itemRepository.findByIdWithPessimisticLock(orderItem.getItem().getNo())
                    .orElseThrow(()-> new ItemNotFoundException(ITEM_NOT_FOUND));
            item.restoreQuantity(orderItem.getQty());
        }
    }

    private boolean isMatchingCartAndOrder(CartItem cartItem, OrderItem orderItem) {
        return cartItem.getItem().getNo().equals(orderItem.getItem().getNo()) // 품목 번호 일치
                && cartItem.getQty().equals(orderItem.getQty());        // 수량 일치
    }
}
