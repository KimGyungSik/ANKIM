package shoppingmall.ankim.domain.payment.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import shoppingmall.ankim.domain.delivery.service.DeliveryService;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.order.repository.OrderRepository;
import shoppingmall.ankim.domain.payment.controller.port.PaymentQueryService;
import shoppingmall.ankim.domain.payment.dto.*;
import shoppingmall.ankim.domain.payment.entity.Payment;
import shoppingmall.ankim.domain.payment.repository.PaymentRepository;
import shoppingmall.ankim.global.config.TossPaymentConfig;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentQueryServiceImpl implements PaymentQueryService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    // 결제 내역
    @Override
    public List<PaymentHistoryResponse> findPaymentHistories(List<String> orderIds) {
        // orderIds로 List<Order> 조회
        List<Order> orders = orderRepository.findByOrdNoIn(orderIds);

        // List<Order>로 List<Payment> 조회
        List<Payment> payments = paymentRepository.findByOrderIn(orders);

        // PaymentHistoryResponse로 맵핑 후 반환
        return payments.stream()
                .map(PaymentHistoryResponse::of)
                .sorted(Comparator.comparing(PaymentHistoryResponse::getPaymentId).reversed()) // 내림차순 정렬
                .collect(Collectors.toList());
    }
}
