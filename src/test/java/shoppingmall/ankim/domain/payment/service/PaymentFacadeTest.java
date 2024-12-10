package shoppingmall.ankim.domain.payment.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;
import shoppingmall.ankim.domain.address.service.request.MemberAddressCreateServiceRequest;
import shoppingmall.ankim.domain.cart.entity.Cart;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.cart.repository.CartRepository;
import shoppingmall.ankim.domain.delivery.entity.Delivery;
import shoppingmall.ankim.domain.delivery.entity.DeliveryStatus;
import shoppingmall.ankim.domain.delivery.repository.DeliveryRepository;
import shoppingmall.ankim.domain.delivery.service.request.DeliveryCreateServiceRequest;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.order.repository.OrderRepository;
import shoppingmall.ankim.domain.orderItem.entity.OrderItem;
import shoppingmall.ankim.domain.orderItem.entity.OrderStatus;
import shoppingmall.ankim.domain.payment.controller.port.PaymentQueryService;
import shoppingmall.ankim.domain.payment.controller.port.PaymentService;
import shoppingmall.ankim.domain.payment.dto.*;
import shoppingmall.ankim.domain.payment.entity.PayType;
import shoppingmall.ankim.domain.payment.entity.Payment;
import shoppingmall.ankim.domain.payment.repository.PaymentRepository;
import shoppingmall.ankim.domain.payment.service.request.PaymentCreateServiceRequest;
import shoppingmall.ankim.factory.OrderFactory;
import shoppingmall.ankim.factory.PaymentFactory;
import shoppingmall.ankim.global.config.S3Config;
import shoppingmall.ankim.global.config.TossPaymentConfig;
import shoppingmall.ankim.global.dummy.InitProduct;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
// TODO DEFINED_PORT로 돌리게 되면 Application 서버가 로딩되면서 InitProduct클래스가 로딩된다.
// TODO 정확히 DEFINED_PORT와 RANDOM_PORT의 차이를 알아야 할듯!
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("prod")
@TestPropertySource(properties = "spring.sql.init.mode=never")
class PaymentFacadeTest {
    @MockBean
    private S3Service s3Service;

    @MockBean
    private InitProduct initProduct;

    @MockBean
    private S3Config s3Config;

    @Autowired
    TossPaymentConfig tossPaymentConfig;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentService paymentServiceImpl;

    @Autowired
    private PaymentFacade paymentFacade;

    @Autowired
    private PaymentQueryService paymentQueryService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TransactionTemplate transactionTemplate;

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

        Order mockOrder = transactionTemplate.execute(status -> {
            Order order = OrderFactory.createOrderWithOutDelivery(entityManager);
            // 영속성 컨텍스트에 포함시키기
            entityManager.persist(order);

            // OrderCode 설정
            order.setOrdCode(orderCode);

            // 변경 사항 DB에 반영
            entityManager.flush();
            entityManager.clear();
            return order;
        });

        assert mockOrder != null; // null 여부 확인

        PaymentCreateServiceRequest request = PaymentCreateServiceRequest.builder()
                .orderName("ORD20241125-1234567")
                .payType(PayType.CARD)
                .amount(50000)
                .build();

        DeliveryCreateServiceRequest deliveryRequest = DeliveryCreateServiceRequest.builder()
                .addressId(null)
                .courier("FastCourier")
                .delReq("문 앞에 놓아주세요")
                .build();

        MemberAddressCreateServiceRequest addressRequest = MemberAddressCreateServiceRequest.builder()
                .addressMain("서울특별시 강남구 테헤란로 123")
                .addressName("집")
                .addressDetail("1층")
                .zipCode(12345)
                .phoneNumber("010-1234-5678")
                .emergencyPhoneNumber("010-5678-1234")
                .defaultAddressYn("Y")
                .build();

        // when
        PaymentResponse response = paymentFacade.createPaymentWithNamedLock(request,deliveryRequest,addressRequest);


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
        Payment savedPayment = paymentRepository.findByOrderNameWithOrder(orderCode).orElseThrow();
        assertThat(savedPayment).isNotNull();
        assertThat(savedPayment.getOrder().getOrdCode()).isEqualTo(orderCode);
        assertThat(savedPayment.getTotalPrice()).isEqualTo(50000L);
        assertThat(savedPayment.getType()).isEqualTo(PayType.CARD);

        // 품목 재고 검증

        // EntityManager를 통해 최신 상태 조회
        List<Item> items = itemRepository.findAll();

        assertThat(items.get(0).getQty()).isEqualTo(98); // 주문 수량 2 감소
        assertThat(items.get(1).getQty()).isEqualTo(97); // 주문 수량 3 감소
    }

    @Test
    @Transactional
    @DisplayName("결제 성공 시 올바른 응답을 반환한다")
    void tossPaymentSuccess() {
        // given
        String paymentKey = "test_payment_key";
        String orderCode = "ORD20241125-1234567";
        Integer amount = 50000;

        // Order와 Payment 데이터 생성 및 저장
        Order order = OrderFactory.createOrderWithDelivery(entityManager);
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
        PaymentSuccessResponse response = paymentServiceImpl.tossPaymentSuccess(paymentKey, order.getOrdNo(), amount);

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

        // 주문 상태 검증
        Order updatedOrder = orderRepository.findByOrdNo(order.getOrdNo()).orElse(null);
        assertThat(updatedOrder).isNotNull();
        assertThat(updatedOrder.getOrderStatus()).isEqualTo(OrderStatus.PAID); // 주문 상태가 결제 완료로 변경되었는지 확인

        // 장바구니 상품 비활성화 검증
        Cart cart = cartRepository.findByMemberAndActiveYn(order.getMember(), "Y").orElse(null);
        assertThat(cart).isNotNull();
        List<CartItem> deactivatedItems = cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getActiveYn().equals("N")) // 비활성화된 상품만 필터링
                .toList();

        // 비활성화된 장바구니 상품이 주문 상품과 일치하는지 확인
        for (OrderItem orderItem : order.getOrderItems()) {
            assertThat(deactivatedItems.stream()
                    .anyMatch(cartItem -> isMatchingCartAndOrder(cartItem, orderItem))
            ).isTrue();
        }

        // Mock 서버 검증
        mockServer.verify();
    }

    private boolean isMatchingCartAndOrder(CartItem cartItem, OrderItem orderItem) {
        return cartItem.getItem().getNo().equals(orderItem.getItem().getNo()) // 품목 번호 일치
                && cartItem.getQty().equals(orderItem.getQty());        // 수량 일치
    }

    @Test
    @Transactional
    @DisplayName("결제 실패 시 에러코드와 에러메세지를 반환하며 주문 상태와 재고 복구가 이루어진다.")
    void tossPaymentFail_shouldReturnPaymentFailResponse() {
        // given
        String orderCode = "ORD20241125-1234567";
        String errorCode = "ERR001";
        String errorMessage = "결제 실패";

        // Order와 Payment 데이터 생성 및 저장
        Order order = OrderFactory.createOrderWithDelivery(entityManager);
        order.setOrdCode(orderCode);
        orderRepository.save(order);

        Payment payment = Payment.create(order, PayType.CARD, 50000);
        paymentRepository.save(payment);

        String orderId = order.getOrdNo(); // 실제 orderId로 설정

        // when
        PaymentFailResponse failResponse = paymentServiceImpl.tossPaymentFail(errorCode, errorMessage, orderId);

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

        // 주문 상태 검증 (결제 실패로 변경)
        Order updatedOrder = orderRepository.findByOrdNo(orderId).orElseThrow(null);
        assertThat(updatedOrder).isNotNull();
        assertThat(updatedOrder.getOrderStatus()).isEqualTo(OrderStatus.FAILED_PAYMENT);

        // 배송 삭제 검증
        assertThat(updatedOrder.getDelivery()).isNull();

        // 재고 복구 검증
        Item item1 = order.getOrderItems().get(0).getItem();
        Item item2 = order.getOrderItems().get(1).getItem();

        // EntityManager를 통해 최신 상태 조회
        Item updatedItem1 = entityManager.find(Item.class, item1.getNo());
        Item updatedItem2 = entityManager.find(Item.class, item2.getNo());

        assertThat(updatedItem1.getQty()).isEqualTo(102); // 100 + 주문 수량 2 복구
        assertThat(updatedItem2.getQty()).isEqualTo(103); // 100 + 주문 수량 3 복구
    }

    @DisplayName("결제 취소 시 성공적으로 PaymentCancelResponse을 반환한다.")
    @Test
    @Transactional
    void cancelPayment() {
        // given
        String paymentKey = "test_payment_key";
        String cancelReason = "사용자 요청";
        String orderCode = "ORD20241125-1234567";
        String expectedCancelDate = "2024-11-25T10:00:00+09:00";

        // Order와 Payment 데이터 생성 및 저장
        Order order = OrderFactory.createOrderWithDelivery(entityManager);
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
        PaymentCancelResponse response = paymentServiceImpl.cancelPayment(paymentKey, cancelReason);

        // then
        assertThat(response.getDetails()).isNotNull();
        assertThat(response.getDetails()).containsKey("details");

        Map innerDetails = (Map) response.getDetails().get("details");
        assertThat(innerDetails).containsEntry("cancelAmount", 50000);
        assertThat(innerDetails).containsEntry("cancelReason", "사용자 요청");
        assertThat(innerDetails).containsEntry("cancelDate", expectedCancelDate);

        // 주문 상태 검증 (결제 취소로 변경)
        Order updatedOrder = orderRepository.findByOrdNo(order.getOrdNo()).orElse(null);
        assertThat(updatedOrder).isNotNull();
        assertThat(updatedOrder.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);

        // 배송 상태 검증 (배송 취소로 변경)
        Delivery updatedDelivery = updatedOrder.getDelivery();
        assertThat(updatedDelivery).isNotNull();
        assertThat(updatedDelivery.getStatus()).isEqualTo(DeliveryStatus.CANCELED);

        // 재고 복구 검증
        Item item1 = order.getOrderItems().get(0).getItem();
        Item item2 = order.getOrderItems().get(1).getItem();

        // EntityManager를 통해 최신 상태 조회
        Item updatedItem1 = entityManager.find(Item.class, item1.getNo());
        Item updatedItem2 = entityManager.find(Item.class, item2.getNo());

        System.out.println("item1.getQty() = " + item1.getQty());
        System.out.println("item2.getQty() = " + item2.getQty());

        assertThat(updatedItem1.getQty()).isEqualTo(102); // 원래 재고 + 복구된 수량 (2)
        assertThat(updatedItem2.getQty()).isEqualTo(103); // 원래 재고 + 복구된 수량 (3)

        // Payment 상태 검증 (결제 취소로 변경)
        Payment canceledPayment = paymentRepository.findByPayKeyWithOrder(paymentKey).orElse(null);
        assertThat(canceledPayment).isNotNull();
        assertThat(canceledPayment.getCancelReason()).isEqualTo(cancelReason);
        assertThat(canceledPayment.isCancelYN()).isTrue();

        // Mock 서버 검증
        mockServer.verify();

    }

    @DisplayName("주문 번호로 결제 내역들을 조회할 수 있다.")
    @Test
    @Transactional
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
        List<PaymentHistoryResponse> result = paymentQueryService.findPaymentHistories(orderIds);

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
