package shoppingmall.ankim.domain.payment.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import shoppingmall.ankim.domain.delivery.entity.Delivery;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.order.repository.OrderRepository;
import shoppingmall.ankim.domain.payment.dto.PaymentResponse;
import shoppingmall.ankim.domain.payment.entity.PayType;
import shoppingmall.ankim.domain.payment.entity.Payment;
import shoppingmall.ankim.domain.payment.repository.PaymentRepository;
import shoppingmall.ankim.domain.payment.service.request.PaymentCreateServiceRequest;
import shoppingmall.ankim.factory.OrderFactory;
import shoppingmall.ankim.global.config.S3Config;
import shoppingmall.ankim.global.config.TossPaymentConfig;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@SpringBootTest
@Transactional
@TestPropertySource(properties = "spring.sql.init.mode=never")
class PaymentServiceTest {
    @MockBean
    private S3Service s3Service;

    @MockBean
    private S3Config s3Config;

    @Autowired
    TossPaymentConfig tossPaymentConfig;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    EntityManager entityManager;

    @DisplayName("클라이언트는 결제 요청을 보낼 수 있다.")
    @Test
    void requestTossPayment_withOrderFactory() {
        // given
        String orderCode = "ORD20241125-1234567";

        // OrderFactory를 통해 Order 생성
        Order mockOrder = OrderFactory.createOrder(entityManager);
        mockOrder.setOrdCode(orderCode);

        // 영속화
        orderRepository.save(mockOrder);

        PaymentCreateServiceRequest request = PaymentCreateServiceRequest.builder()
                .orderCode(orderCode)
                .payType(PayType.CARD)
                .amount(50000)
                .build();

        // when
        PaymentResponse response = paymentService.requestTossPayment(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getOrderName()).isEqualTo(orderCode);
        assertThat(response.getCustomerEmail()).isEqualTo(mockOrder.getMember().getLoginId());
        assertThat(response.getCustomerName()).isEqualTo(mockOrder.getMember().getName());
        assertThat(response.getAmount()).isEqualTo(50000L);
        assertThat(response.getPayType()).isEqualTo(PayType.CARD);
        assertThat(response.getSuccessUrl()).isEqualTo(tossPaymentConfig.getSuccessUrl());
        assertThat(response.getFailUrl()).isEqualTo(tossPaymentConfig.getFailUrl());

        // Payment 저장 검증
        List<Payment> payments = paymentRepository.findAll();
        assertThat(payments).hasSize(1);
        Payment savedPayment = payments.get(0);
        assertThat(savedPayment.getOrder().getOrdCode()).isEqualTo(orderCode);
        assertThat(savedPayment.getTotalPrice()).isEqualTo(50000L);
        assertThat(savedPayment.getType()).isEqualTo(PayType.CARD);
    }
}
