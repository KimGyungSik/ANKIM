package shoppingmall.ankim.domain.payment.service;

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

    @MockBean
    private OrderRepository orderRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @DisplayName("클라이언트는 결제 요청을 보낼 수 있다.")
    @Test
    void requestTossPayment_withMockedOrderAndMember() {
        // given
        String orderCode = "ORD20241125-1234567";

        // Mock Member 생성
        Member mockMember = mock(Member.class);
        given(mockMember.getName()).willReturn("김경식");
        given(mockMember.getLoginId()).willReturn("0711kyung@naver.com");

        // Mock Order 생성
        Order mockOrder = mock(Order.class);
        given(mockOrder.getOrdCode()).willReturn(orderCode);
        given(mockOrder.getMember()).willReturn(mockMember);

        // Mock OrderRepository 동작 설정
        given(orderRepository.findByOrderCodeWithMember(orderCode)).willReturn(Optional.of(mockOrder));

        PaymentCreateServiceRequest request = PaymentCreateServiceRequest.builder()
                .orderCode(orderCode)
                .payType(PayType.CARD)
                .amount(50000)
                .build();

        // when
        PaymentResponse response = paymentService.requestTossPayment(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getOrderCode()).isEqualTo(orderCode);
        assertThat(response.getCustomerEmail()).isEqualTo(mockMember.getLoginId());
        assertThat(response.getCustomerName()).isEqualTo(mockMember.getName());
        assertThat(response.getAmount()).isEqualTo(50000L);
        assertThat(response.getPayType()).isEqualTo(PayType.CARD);
        assertThat(response.getSuccessUrl()).isEqualTo(tossPaymentConfig.getSuccessUrl());
        assertThat(response.getFailUrl()).isEqualTo(tossPaymentConfig.getFailUrl());

//        // Payment 저장 검증
//        List<Payment> payments = paymentRepository.findAll();
//        assertThat(payments).hasSize(1);
//        Payment savedPayment = payments.get(0);
//        assertThat(savedPayment.getOrder().getOrdCode()).isEqualTo(orderCode);
//        assertThat(savedPayment.getTotalPrice()).isEqualTo(50000L);
//        assertThat(savedPayment.getType()).isEqualTo(PayType.CARD);
    }
}
