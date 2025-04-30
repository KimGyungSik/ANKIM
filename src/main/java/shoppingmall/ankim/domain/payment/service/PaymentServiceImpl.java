package shoppingmall.ankim.domain.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import shoppingmall.ankim.domain.address.service.request.MemberAddressCreateServiceRequest;
import shoppingmall.ankim.domain.delivery.dto.DeliveryResponse;
import shoppingmall.ankim.domain.delivery.events.DeliveryCreateRequestedEvent;
import shoppingmall.ankim.domain.delivery.service.request.DeliveryCreateServiceRequest;
import shoppingmall.ankim.domain.item.events.StockReduceRequestedEvent;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.order.exception.OrderNotFoundException;
import shoppingmall.ankim.domain.order.repository.OrderRepository;
import shoppingmall.ankim.domain.orderItem.entity.OrderStatus;
import shoppingmall.ankim.domain.payment.controller.port.PaymentService;
import shoppingmall.ankim.domain.payment.dto.*;
import shoppingmall.ankim.domain.payment.entity.Payment;
import shoppingmall.ankim.domain.payment.events.PaymentFailedEvent;
import shoppingmall.ankim.domain.payment.events.PaymentSuccessProcessedEvent;
import shoppingmall.ankim.domain.payment.exception.AlreadyApprovedException;
import shoppingmall.ankim.domain.payment.exception.PaymentAmountNotEqualException;
import shoppingmall.ankim.domain.payment.exception.PaymentNotFoundException;
import shoppingmall.ankim.domain.payment.repository.PaymentRepository;
import shoppingmall.ankim.domain.payment.service.request.PaymentCreateServiceRequest;
import shoppingmall.ankim.global.config.TossPaymentConfig;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final TossPaymentConfig tossPaymentConfig;
    private final RestTemplate restTemplate;
    private final ApplicationEventPublisher eventPublisher;

    // Payment 저장 실패 -> Delivery 저장 실패
    // Delivery 저장 실패 -> Payment 저장 성공
    @Override
    public PaymentResponse requestTossPayment(PaymentCreateServiceRequest request,
                                              DeliveryCreateServiceRequest deliveryRequest,
                                              MemberAddressCreateServiceRequest addressRequest) {
        // Order 조회
        Order order = orderRepository.findByOrdName(request.getOrderName())
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));

        // 2. 결제 대기 상태 확인
        if (order.getOrderStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new AlreadyApprovedException(ALREADY_APPROVED);
        }

        // 3. Payment 생성 & 저장
        Payment payment = paymentRepository.save(Payment.create(order, request.getPayType(), request.getAmount()));

        // 4. PaymentResponse 변환
        PaymentResponse response = PaymentResponse.of(payment);
        response.setSuccessUrl(request.getYourSuccessUrl() == null ? tossPaymentConfig.getSuccessUrl() : request.getYourSuccessUrl());
        response.setFailUrl(request.getYourFailUrl() == null ? tossPaymentConfig.getFailUrl() : request.getYourFailUrl());

        // 5. 배송지 생성 이벤트 발행
        eventPublisher.publishEvent(new DeliveryCreateRequestedEvent(
                order,
                deliveryRequest,
                addressRequest
        ));

        return response;
    }

    @Override
    public PaymentSuccessResponse tossPaymentSuccess(String paymentKey, String orderId, Integer amount) {
        Order order = orderRepository.findByOrderNoWithMemberAndOrderItemsAndItem(orderId)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
        // 1. 재고 차감 이벤트 발생
            // 1-1. 실패 시 재고 복구 이벤트 발행
            // 1-2. 배송지 삭제 이벤트 발행
        eventPublisher.publishEvent(new StockReduceRequestedEvent(order));
        // 2. 결제 성공 시 처리
        Payment payment = verifyPayment(orderId, amount);
        PaymentSuccessResponse result = requestPaymentAccept(paymentKey, orderId, amount);
        payment.setPaymentKey(paymentKey, true);

        result.setPaymentSuccessInfoResponse(PaymentSuccessInfoResponse.builder()
                .totalShipFee(order.getTotalShipFee())
                .deliveryResponse(DeliveryResponse.of(order.getDelivery()))
                .build());

        // 3. 주문 상태 -> 결제 완료로 변경 ( 이벤트 발행 )
        // 4. 주문 상품 장바구니에서 비우기 ( 이벤트 발행 )
        eventPublisher.publishEvent(new PaymentSuccessProcessedEvent(orderId));
        return result;
    }

    @Override
    public PaymentFailResponse tossPaymentFail(String code, String message, String orderId) {
        // 1. 결제 실패 시 처리
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() -> new PaymentNotFoundException(PAYMENT_NOT_FOUND));
        payment.setFailReason(message, false);

        // 2. 주문 상태 -> 결제 실패 & 배송지 삭제 이벤트 발행
        // 3. 재고 복구 이벤트 발행
        eventPublisher.publishEvent(new PaymentFailedEvent(orderId));

        return PaymentFailResponse.of(code, message, orderId);
    }
    @Override
    public PaymentCancelResponse cancelPayment(String orderId, String cancelReason) {
        // 결제 취소 시 처리
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() -> new PaymentNotFoundException(PAYMENT_NOT_FOUND));
        payment.setPaymentCancel(cancelReason, true);
        Map map = tossPaymentCancel(payment.getPayKey(), cancelReason);
        return PaymentCancelResponse.builder().details(map).build();
    }


    private Map tossPaymentCancel(String paymentKey, String cancelReason) {
        HttpHeaders headers = getHeaders();
        JSONObject params = new JSONObject();
        params.put("cancelReason", cancelReason);

        return restTemplate.postForObject(TossPaymentConfig.URL + paymentKey + "/cancel",
                new HttpEntity<>(params, headers),
                Map.class);
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
