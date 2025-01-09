package shoppingmall.ankim.domain.delivery.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;
import shoppingmall.ankim.domain.address.entity.BaseAddress;
import shoppingmall.ankim.domain.address.entity.member.MemberAddress;
import shoppingmall.ankim.domain.delivery.entity.Delivery;
import shoppingmall.ankim.domain.delivery.entity.DeliveryStatus;
import shoppingmall.ankim.domain.delivery.repository.DeliveryRepository;
import shoppingmall.ankim.domain.delivery.service.DeliveryStatusUpdateService;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.order.repository.OrderRepository;
import shoppingmall.ankim.domain.orderItem.entity.OrderItem;
import shoppingmall.ankim.domain.orderItem.entity.OrderStatus;
import shoppingmall.ankim.domain.orderItem.repository.OrderItemRepository;
import shoppingmall.ankim.domain.payment.entity.PayType;
import shoppingmall.ankim.domain.payment.entity.Payment;
import shoppingmall.ankim.domain.payment.repository.PaymentRepository;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.factory.MemberJwtFactory;
import shoppingmall.ankim.factory.OrderFactory;
import shoppingmall.ankim.factory.ProductFactory;
import shoppingmall.ankim.global.config.S3Config;
import shoppingmall.ankim.global.config.TestClockConfig;
import shoppingmall.ankim.global.config.clock.ClockHolder;
import shoppingmall.ankim.global.config.track.TrackingNumberGenerator;
import shoppingmall.ankim.global.dummy.InitProduct;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestClockConfig.class)
@ActiveProfiles("prod")
@TestPropertySource(properties = "spring.sql.init.mode=never")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DeliveryStatusUpdateConcurrencyServiceTest {
    @MockBean
    private S3Service s3Service;

    @MockBean
    private InitProduct initProduct;

    @MockBean
    private S3Config s3Config;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ClockHolder clockHolder;

    @Autowired
    private DeliveryStatusUpdateService updateService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    private List<String> orderIds = new ArrayList<>();
    private List<String> paymentkeys = new ArrayList<>();

    @BeforeEach
    void setUp() {
            mockServer = MockRestServiceServer.createServer(restTemplate);
            transactionTemplate.execute(status -> {
                try {
                    // Member와 Product 생성
                    Member member = MemberJwtFactory.createMember(entityManager, "test-user@domain.com");
                    Product product = ProductFactory.createProduct(entityManager);
                    entityManager.persist(product);

                    Item item = product.getItems().get(0);
                    Item item2 = product.getItems().get(1);

                    // 결제 실패, 취소에 대한 더미 (재고 복구)
                    for (int i = 0; i < 100; i++) {
                        OrderItem orderItem = OrderItem.create(item, 1);
                        OrderItem orderItem2 = OrderItem.create(item2, 1);
                        entityManager.persist(orderItem);
                        entityManager.persist(orderItem2);

                        String dynamicOrderName = "ORD20241130-" + String.format("%07d", i);

                        MemberAddress address = MemberAddress.create(
                                member,
                                "집",
                                BaseAddress.builder()
                                        .zipCode(12345)
                                        .addressMain("서울시 강남구 테헤란로 123")
                                        .addressDetail("1층")
                                        .build(),
                                "010-1234-5678",
                                "010-5678-1234",
                                "Y"
                        );
                        entityManager.persist(address);

                        Delivery delivery = Delivery.create(
                                address,
                                "FastCourier",
                                "문 앞에 놓아주세요.",
                                new TrackingNumberGenerator() {
                                    @Override
                                    public String generate() {
                                        return "TRACK123456";
                                    }
                                }
                        );
                        delivery.setStatus(DeliveryStatus.RETURN_REQUESTED);
                        deliveryRepository.save(delivery);

                        LocalDateTime now = LocalDateTime.ofInstant(Instant.ofEpochMilli(clockHolder.millis()), ZoneId.systemDefault());
                        Order order = Order.create(List.of(orderItem,orderItem2), member, delivery, now.minusDays(6));
                        order.setOrdCode(dynamicOrderName);
                        order.setOrderStatus(OrderStatus.PAID);
                        orderRepository.save(order);
                        orderIds.add(order.getOrdNo());

                        String paymentKey = "test_key" + String.format("%07d", i);
                        paymentkeys.add(paymentKey);
                        Payment payment = Payment.create(order, PayType.CARD, 50000);
                        payment.setPaymentKey(paymentKey, true);
                        payment.setCancelReason("반품");

                        paymentRepository.save(payment);
                    }

                    // flush를 통해 DB에 반영
                    entityManager.flush();
                    entityManager.clear();
                } catch (Exception e) {
                    // 예외가 발생하면 rollback
                    status.setRollbackOnly();
                    throw e;
                }
                return null;
            });
    }

    @DisplayName("100명의 사용자가 동일한 상품에 대하여 반품 요청을 한 경우 3일 후에 재고 복구 및 배송 상태를 '반품완료'로 변경할 수 있다.")
    @Test
    void updateDeliveryStatusesWithRETURN_COMPLETED() {
        // given
        mockServer.expect(ExpectedCount.manyTimes(), MockRestRequestMatchers.anything())
                .andRespond(MockRestResponseCreators.withSuccess("{\"status\": \"success\"}", MediaType.APPLICATION_JSON));

        updateService.updateDeliveryStatuses();

        Item item = itemRepository.findById(1L).orElseThrow();
        Item item2 = itemRepository.findById(2L).orElseThrow();

        // 정합성 검증
        assertThat(item.getQty()).isEqualTo(100);
        assertThat(item2.getQty()).isEqualTo(100);
    }
}