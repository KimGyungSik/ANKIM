package shoppingmall.ankim.domain.payment.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import shoppingmall.ankim.domain.delivery.entity.Delivery;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.order.repository.OrderRepository;
import shoppingmall.ankim.domain.payment.dto.*;
import shoppingmall.ankim.domain.payment.entity.PayType;
import shoppingmall.ankim.domain.payment.entity.Payment;
import shoppingmall.ankim.domain.payment.exception.AlreadyApprovedException;
import shoppingmall.ankim.domain.payment.repository.PaymentRepository;
import shoppingmall.ankim.domain.payment.service.request.PaymentCreateServiceRequest;
import shoppingmall.ankim.factory.OrderFactory;
import shoppingmall.ankim.factory.PaymentFactory;
import shoppingmall.ankim.global.config.S3Config;
import shoppingmall.ankim.global.config.TossPaymentConfig;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Transactional
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.sql.init.mode=never")
class  PaymentServiceTest {
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
    private EntityManager entityManager;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

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
                .orderName(orderCode)
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
        assertThat(response.getPayType()).isEqualTo("카드");
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

    @Test
    @DisplayName("결제 성공 시 올바른 응답을 반환한다")
    void tossPaymentSuccess() {
        // given
        String paymentKey = "test_payment_key";
        String orderCode = "ORD20241125-1234567";
        Integer amount = 50000;

        // Order와 Payment 데이터 생성 및 저장
        Order order = OrderFactory.createOrder(entityManager);
        order.setOrdCode(orderCode);
        orderRepository.save(order);

        Payment payment = Payment.create(order, PayType.CARD, amount);
        paymentRepository.save(payment);

        // orderId에 order.getOrdNo()를 동적으로 삽입
        String expectedResponse = String.format("""
    {
        "orderId": "%s",
        "amount": 50000,
        "status": "SUCCESS"
    }
    """, order.getOrdNo());

        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/" + paymentKey))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        // when
        PaymentSuccessResponse response = paymentService.tossPaymentSuccess(paymentKey, order.getOrdNo(), amount);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo(order.getOrdNo());
        assertThat(response.getAmount()).isEqualTo(amount.toString());
        assertThat(response.getStatus()).isEqualTo("SUCCESS");

        // Payment 검증
        Payment savedPayment = paymentRepository.findByOrderId(order.getOrdNo()).orElse(null);
        assertThat(savedPayment).isNotNull();
        assertThat(savedPayment.getPayKey()).isEqualTo(paymentKey);
        assertThat(savedPayment.isPaySuccessYN()).isTrue();

        // Mock 서버 검증
        mockServer.verify();
    }

    @Test
    @DisplayName("결제 실패 시 에러코드와 에러메세지를 반환한다.")
    void tossPaymentFail_shouldReturnPaymentFailResponse() {
        // given
        String orderCode = "ORD20241125-1234567";
        String errorCode = "ERR001";
        String errorMessage = "결제 실패";

        // Order와 Payment 데이터 생성 및 저장
        Order order = OrderFactory.createOrder(entityManager);
        order.setOrdCode(orderCode);
        orderRepository.save(order);

        Payment payment = Payment.create(order, PayType.CARD, 50000);
        paymentRepository.save(payment);

        String orderId = order.getOrdNo(); // 실제 orderId로 설정

        // when
        PaymentFailResponse failResponse = paymentService.tossPaymentFail(errorCode, errorMessage, orderId);

        // then
        assertThat(failResponse).isNotNull();
        assertThat(failResponse.getErrorCode()).isEqualTo(errorCode);
        assertThat(failResponse.getErrorMessage()).isEqualTo(errorMessage);
        assertThat(failResponse.getOrderId()).isEqualTo(orderId);

        // Payment 실패 상태 검증
        Payment failedPayment = paymentRepository.findByOrderId(orderId).orElse(null);
        assertThat(failedPayment).isNotNull();
        assertThat(failedPayment.getFailReason()).isEqualTo(errorMessage);
        assertThat(failedPayment.isPaySuccessYN()).isFalse();
    }


    @DisplayName("결제 취소 시 성공적으로 PaymentCancelResponse을 반환한다.")
    @Test
    void cancelPayment() {
        // given
        String paymentKey = "test_payment_key";
        String cancelReason = "사용자 요청";
        String orderCode = "ORD20241125-1234567";
        String expectedCancelDate = "2024-11-25T10:00:00+09:00";

        // Order와 Payment 데이터 생성 및 저장
        Order order = OrderFactory.createOrder(entityManager);
        order.setOrdCode(orderCode);
        orderRepository.save(order);

        Payment payment = Payment.create(order, PayType.CARD, 50000);
        payment.setPaymentKey(paymentKey,true);
        paymentRepository.save(payment);

        // Mock Toss API 응답
        String expectedResponse = """
    {
        "details": {
            "cancelAmount": 50000,
            "cancelDate": "2024-11-25T10:00:00+09:00",
            "cancelReason": "사용자 요청"
        }
    }
    """;

        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        // when
        PaymentCancelResponse response = paymentService.cancelPayment(paymentKey, cancelReason);

        // then
        assertThat(response.getDetails()).isNotNull();
        assertThat(response.getDetails()).containsKey("details");

        // Extract the nested map and validate its contents
        Map innerDetails = (Map) response.getDetails().get("details");
        assertThat(innerDetails).containsEntry("cancelAmount", 50000);
        assertThat(innerDetails).containsEntry("cancelReason", "사용자 요청");
        assertThat(innerDetails).containsEntry("cancelDate", expectedCancelDate);

        // Mock 서버 검증
        mockServer.verify();
    }

    @DisplayName("주문 번호로 결제 내역들을 조회할 수 있다.")
    @Test
    void findPaymentHistories() {
        // given
        int count = 5; // 결제 5개 생성

        List<Payment> payments = PaymentFactory.createPayments(entityManager, count);
        paymentRepository.saveAll(payments);

        List<String> orderIds = payments.stream()
                .map(payment -> payment.getOrder().getOrdNo())
                .toList();

        // 내림차순 정렬된 기대값 준비
        List<Payment> sortedPayments = payments.stream()
                .sorted(Comparator.comparing(Payment::getNo).reversed()) // paymentId 기준 내림차순
                .toList();

        // when
        List<PaymentHistoryResponse> result = paymentService.findPaymentHistories(orderIds);

        // then
        assertThat(result).hasSize(5);

        // 각 필드 검증 (내림차순 기준)
        assertThat(result)
                .extracting(PaymentHistoryResponse::getPaymentId)
                .containsExactlyElementsOf(sortedPayments.stream().map(Payment::getNo).toList());

        assertThat(result)
                .extracting(PaymentHistoryResponse::getTotalPrice)
                .containsExactlyElementsOf(sortedPayments.stream().map(Payment::getTotalPrice).toList());

        assertThat(result)
                .extracting(PaymentHistoryResponse::getOrderName)
                .containsExactlyElementsOf(sortedPayments.stream().map(payment -> payment.getOrder().getOrdCode()).toList());

        assertThat(result)
                .extracting(PaymentHistoryResponse::getType)
                .containsExactlyElementsOf(sortedPayments.stream().map(payment -> payment.getType().getDescription()).toList());

        assertThat(result)
                .extracting(PaymentHistoryResponse::isPaySuccessYN)
                .containsExactlyElementsOf(sortedPayments.stream().map(Payment::isPaySuccessYN).toList());

        assertThat(result)
                .extracting(PaymentHistoryResponse::getCreatedAt)
                .containsExactlyElementsOf(sortedPayments.stream().map(Payment::getCreatedAt).toList());
    }


}
