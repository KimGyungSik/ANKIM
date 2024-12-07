package shoppingmall.ankim.domain.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
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
import shoppingmall.ankim.domain.orderItem.entity.OrderItem;
import shoppingmall.ankim.domain.payment.controller.port.PaymentService;
import shoppingmall.ankim.domain.payment.dto.*;
import shoppingmall.ankim.domain.payment.entity.Payment;
import shoppingmall.ankim.domain.payment.exception.AlreadyApprovedException;
import shoppingmall.ankim.domain.payment.exception.PaymentAmountNotEqualException;
import shoppingmall.ankim.domain.payment.exception.PaymentNotFoundException;
import shoppingmall.ankim.domain.payment.repository.PaymentRepository;
import shoppingmall.ankim.domain.payment.service.request.PaymentCreateServiceRequest;
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
    private final TossPaymentConfig tossPaymentConfig;
    private final RestTemplate restTemplate;

    @Override
    public PaymentResponse requestTossPayment(PaymentCreateServiceRequest request) {
        // Order 조회
        Order order = orderRepository.findByOrdName(request.getOrderName())
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));

        // Payment 생성 & 저장
        Payment payment = paymentRepository.save(Payment.create(order, request.getPayType(), request.getAmount()));

        // PaymentResponse 변환
        PaymentResponse response = PaymentResponse.of(payment);
        response.setSuccessUrl(request.getYourSuccessUrl() == null ? tossPaymentConfig.getSuccessUrl() : request.getYourSuccessUrl());
        response.setFailUrl(request.getYourFailUrl() == null ? tossPaymentConfig.getFailUrl() : request.getYourFailUrl());

        return response;
    }

    @Override
    public PaymentSuccessResponse tossPaymentSuccess(String paymentKey, String orderId, Integer amount) {
        // 결제 성공 시 처리
        Payment payment = verifyPayment(orderId, amount);
        PaymentSuccessResponse result = requestPaymentAccept(paymentKey, orderId, amount);
        payment.setPaymentKey(paymentKey, true);
        return result;
    }
    @Override
    public PaymentFailResponse tossPaymentFail(String code, String message, String orderId) {
        // 결제 실패 시 처리
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() -> new PaymentNotFoundException(PAYMENT_NOT_FOUND));
        payment.setFailReason(message, false);
        return PaymentFailResponse.of(code, message, orderId);
    }
    @Override
    public PaymentCancelResponse cancelPayment(String paymentKey, String cancelReason) {
        // 결제 취소 시 처리
        Payment payment = paymentRepository.findByPayKeyWithOrder(paymentKey).orElseThrow(() -> new PaymentNotFoundException(PAYMENT_NOT_FOUND));
        payment.setPaymentCancel(cancelReason, true);
        Map map = tossPaymentCancel(paymentKey, cancelReason);
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
