package shoppingmall.ankim.domain.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import shoppingmall.ankim.domain.address.service.request.MemberAddressCreateServiceRequest;
import shoppingmall.ankim.domain.cart.entity.Cart;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.cart.exception.CartNotFoundException;
import shoppingmall.ankim.domain.cart.repository.CartRepository;
import shoppingmall.ankim.domain.delivery.entity.Delivery;
import shoppingmall.ankim.domain.delivery.service.DeliveryService;
import shoppingmall.ankim.domain.delivery.service.request.DeliveryCreateServiceRequest;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.item.exception.ItemNotFoundException;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
import shoppingmall.ankim.domain.item.service.ItemService;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.order.exception.OrderNotFoundException;
import shoppingmall.ankim.domain.order.repository.OrderRepository;
import shoppingmall.ankim.domain.order.service.OrderQueryService;
import shoppingmall.ankim.domain.orderItem.entity.OrderItem;
import shoppingmall.ankim.domain.orderItem.repository.OrderItemRepository;
import shoppingmall.ankim.domain.payment.controller.port.PaymentService;
import shoppingmall.ankim.domain.payment.dto.*;
import shoppingmall.ankim.domain.payment.entity.Payment;
import shoppingmall.ankim.domain.payment.exception.AlreadyApprovedException;
import shoppingmall.ankim.domain.payment.exception.PaymentAmountNotEqualException;
import shoppingmall.ankim.domain.payment.exception.PaymentNotFoundException;
import shoppingmall.ankim.domain.payment.repository.PaymentRepository;
import shoppingmall.ankim.domain.payment.service.request.PaymentCreateServiceRequest;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.global.config.TossPaymentConfig;
import shoppingmall.ankim.global.config.lock.LockHandler;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static shoppingmall.ankim.domain.orderItem.entity.OrderStatus.*;
import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;
    private final OrderItemRepository orderItemRepository;
    private final TossPaymentConfig tossPaymentConfig;
    private final RestTemplate restTemplate;
    private final OrderQueryService orderQueryService;
    private final DeliveryService deliveryService;
    private final ItemService itemService;
    private final LockHandler lockHandler;

    // 클라이언트 결제 요청처리 & 재고 감소 & 배송지 저장
    @Override
    public PaymentResponse requestTossPayment(PaymentCreateServiceRequest request,
                                              DeliveryCreateServiceRequest deliveryRequest,
                                              MemberAddressCreateServiceRequest addressRequest) {
        // Order 조회 (fetch join으로 Member 로딩)
        Order order = orderRepository.findByOrderNameWithMemberAndOrderItems(request.getOrderName())
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));

        // 결제 대기중 상태가 아니라면 이미 승인된 결제이므로 예외 발생
        if (order.getOrderStatus() != PENDING_PAYMENT) {
            throw new AlreadyApprovedException(ALREADY_APPROVED);
        }

        // 배송지 생성
        Delivery delivery = deliveryService.createDelivery(deliveryRequest, addressRequest, order.getMember().getLoginId());
        order.setDelivery(delivery);

        // OrderItem별로 처리
        for (OrderItem orderItem : order.getOrderItems()) {
            Long itemNo = orderItem.getItem().getNo();
            Integer quantity = orderItem.getQty();
            log.debug("Reducing stock for itemNo: {}, quantity: {}", itemNo, quantity);
            String key = String.valueOf(itemNo);
            try {
                // 아이템별 락
                lockHandler.lock(key);

                // 아이템 단위로 재고 감소
                itemService.reduceStock(itemNo, quantity);
            } finally {
                // 락 해제
                lockHandler.unlock(key);
            }
        }

        // Payment 생성 & 저장
        Payment payment = paymentRepository.save(Payment.create(order, request.getPayType(), request.getAmount()));

        // PaymentResponse 변환
        PaymentResponse response = PaymentResponse.of(payment);
        response.setSuccessUrl(request.getYourSuccessUrl() == null ? tossPaymentConfig.getSuccessUrl() : request.getYourSuccessUrl());
        response.setFailUrl(request.getYourFailUrl() == null ? tossPaymentConfig.getFailUrl() : request.getYourFailUrl());

        return response;
    }

    // 결제 성공 시 처리 & 주문 상태 (결제완료) & 장바구니 주문 상품 비활성화 (장바구니 비우기)
    @Override
    public PaymentSuccessResponse tossPaymentSuccess(String paymentKey, String orderId, Integer amount) {
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

        // 결제 성공 시 처리
        Payment payment = verifyPayment(orderId, amount);
        PaymentSuccessResponse result = requestPaymentAccept(paymentKey, orderId, amount);
        payment.setPaymentKey(paymentKey, true);
        return result;
    }

    // 결제 실패 시 처리 & 재고 복구 & 주문 상태 (결제실패) & 배송 삭제
    @Override
    public PaymentFailResponse tossPaymentFail(String code, String message, String orderId) {
        // 주문 상태를 결제실패로 수정 & 배송지 삭제
        Order order = orderRepository.findByOrderIdWithMemberAndDeliveryAndOrderItems(orderId)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
        order.failOrderWithOutDelivery();

        // 재고 복구
        restoreStock(order.getOrderItems());

        // 결제 실패 시 처리
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() -> new PaymentNotFoundException(PAYMENT_NOT_FOUND));
        payment.setFailReason(message, false);
        return PaymentFailResponse.of(code, message, orderId);
    }

    // 결제 취소 시 처리 & 재고 복구 & 주문 상태 (결제취소) & 배송 상태 (배송 취소)
    @Override
    public PaymentCancelResponse cancelPayment(String paymentKey, String cancelReason) {
        Payment payment = paymentRepository.findByPayKeyWithOrder(paymentKey).orElseThrow(() -> new PaymentNotFoundException(PAYMENT_NOT_FOUND));

        Order order = orderRepository.findByOrderIdWithMemberAndDeliveryAndOrderItems(payment.getOrder().getOrdNo())
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));;
        // 단, 결제 취소 시 배송 상태가 배송 준비 상태일때만 가능
        order.cancelOrder(); // 주문 및 배송 취소 처리

        // 재고 복구
        restoreStock(order.getOrderItems());

        // 결제 취소 시 처리
        payment.setPaymentCancel(cancelReason, true);
        Map map = tossPaymentCancel(paymentKey, cancelReason);
        return PaymentCancelResponse.builder().details(map).build();
    }


    private void restoreStock(List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            // 비관적 락으로 Item 조회
            Item item = itemRepository.findByIdWithPessimisticLock(orderItem.getItem().getNo())
                    .orElseThrow(() -> new ItemNotFoundException(ITEM_NOT_FOUND));
            Integer quantity = orderItem.getQty(); // 주문 수량 가져오기
            item.restoreQuantity(quantity); // Item의 재고 복구 메서드 호출
        }
    }


    private Map tossPaymentCancel(String paymentKey, String cancelReason) {
        HttpHeaders headers = getHeaders();
        JSONObject params = new JSONObject();
        params.put("cancelReason", cancelReason);

        return restTemplate.postForObject(TossPaymentConfig.URL + paymentKey + "/cancel",
                new HttpEntity<>(params, headers),
                Map.class);
    }

    private boolean isMatchingCartAndOrder(CartItem cartItem, OrderItem orderItem) {
        return cartItem.getItem().getNo().equals(orderItem.getItem().getNo()) // 품목 번호 일치
                && cartItem.getQty().equals(orderItem.getQty());        // 수량 일치
    }

    private PaymentSuccessResponse requestPaymentAccept(String paymentKey, String orderId, Integer amount) {
        HttpHeaders headers = getHeaders();
        JSONObject params = new JSONObject();
        params.put("orderId", orderId);
        params.put("amount", amount);

        PaymentSuccessResponse result = null;
        try {
            result = restTemplate.postForObject(TossPaymentConfig.URL + paymentKey,
                    new HttpEntity<>(params, headers),
                    PaymentSuccessResponse.class);
        } catch (Exception e) {
            throw new AlreadyApprovedException(ALREADY_APPROVED);
        }

        return result;
    }

    private Payment verifyPayment(String orderId, Integer amount) {
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() -> new PaymentNotFoundException(PAYMENT_NOT_FOUND));
        if (!payment.getTotalPrice().equals(amount)) {
            throw new PaymentAmountNotEqualException(PAYMENT_AMOUNT_EXP);
        }
        return payment;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String encodedAuthKey = new String(
                Base64.getEncoder().encode((tossPaymentConfig.getTestSecretKey() + ":").getBytes(StandardCharsets.UTF_8)));
        headers.setBasicAuth(encodedAuthKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }
}
