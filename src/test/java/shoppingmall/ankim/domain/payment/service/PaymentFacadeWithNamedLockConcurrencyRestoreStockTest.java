package shoppingmall.ankim.domain.payment.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import shoppingmall.ankim.domain.address.repository.MemberAddressRepository;
import shoppingmall.ankim.domain.delivery.entity.Delivery;
import shoppingmall.ankim.domain.delivery.repository.DeliveryRepository;
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
import shoppingmall.ankim.domain.payment.controller.port.PaymentQueryService;
import shoppingmall.ankim.domain.payment.entity.PayType;
import shoppingmall.ankim.domain.payment.entity.Payment;
import shoppingmall.ankim.domain.payment.repository.PaymentRepository;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.repository.ProductRepository;
import shoppingmall.ankim.factory.MemberJwtFactory;
import shoppingmall.ankim.factory.ProductFactory;
import shoppingmall.ankim.global.config.S3Config;
import shoppingmall.ankim.global.config.TossPaymentConfig;
import shoppingmall.ankim.global.config.track.TrackingNumberGenerator;
import shoppingmall.ankim.global.dummy.InitProduct;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("prod")
@TestPropertySource(properties = "spring.sql.init.mode=never")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PaymentFacadeWithNamedLockConcurrencyRestoreStockTest {
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
    private PaymentFacadeWithNamedLock paymentFacadeWithNamedLock;

    @Autowired
    private PaymentQueryService paymentQueryService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MemberAddressRepository memberAddressRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    private List<String> orderNames = new ArrayList<>();
    private List<String> orderIds = new ArrayList<>();
    private List<String> paymentkeys = new ArrayList<>();

    @BeforeEach
    void setUp() {
        if (!orderRepository.existsByOrdCode("ORD20241130-0000000")) {
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
                        entityManager.persist(delivery);

                        Order order = Order.create(List.of(orderItem,orderItem2), member, delivery, LocalDateTime.now());
                        order.setOrdCode(dynamicOrderName);
                        order.setOrderStatus(OrderStatus.PAID);
                        orderRepository.save(order);
                        orderIds.add(order.getOrdNo());

                        String paymentKey = "test_key" + String.format("%07d", i);
                        paymentkeys.add(paymentKey);
                        Payment payment = Payment.create(order, PayType.CARD, 50000);
                        payment.setPaymentKey(paymentKey, true);

                        paymentRepository.save(payment);
                    }

                    // flush를 통해 DB에 반영
                    entityManager.flush();
                    entityManager.clear();
                } catch (Exception e) {
                    // 데이터 준비가 완료될 때까지 대기
                    while (!orderRepository.existsByOrdCode("ORD20241130-0000000")) {
                        try {
                            Thread.sleep(100); // 데이터 준비 대기
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    // 예외가 발생하면 rollback
                    status.setRollbackOnly();
                    throw e;
                }
                return null;
            });
        } else { // 데이터가 이미 존재하는 경우 orderNames 리스트를 초기화
            // 결제 요청에 대한 더미 (재고 감소)
            mockServer = MockRestServiceServer.createServer(restTemplate);
            transactionTemplate.execute(status -> {
                Member member = memberRepository.findByLoginId("test-user@domain.com");
                Item item = itemRepository.findById(1L).orElseThrow();
                Item item2 = itemRepository.findById(2L).orElseThrow();
                for (int i = 0; i < 100; i++) {
                    OrderItem orderItem = OrderItem.create(item, 1);
                    OrderItem orderItem2 = OrderItem.create(item2, 1);
                    entityManager.persist(orderItem);
                    entityManager.persist(orderItem2);
                    Long itemNo1 = orderItem.getNo();
                    Long itemNo2 = orderItem2.getNo();

                    String dynamicOrderName = "ORD20251132-" + UUID.randomUUID().toString().substring(0, 6);

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
                    entityManager.persist(delivery);
                    Long id = delivery.getNo();
                    entityManager.flush();
                    entityManager.clear();


                    Delivery save = deliveryRepository.findById(id).orElseThrow();
                    OrderItem saveOrderItem1 = orderItemRepository.findById(itemNo1).orElseThrow();
                    OrderItem saveOrderItem2 = orderItemRepository.findById(itemNo2).orElseThrow();

                    Order order = Order.create(List.of(saveOrderItem1, saveOrderItem2), member, save, LocalDateTime.now());
                    order.setOrdCode(dynamicOrderName);
//                    orderRepository.save(order);
                    entityManager.persist(order);
                    entityManager.flush();
                    entityManager.clear();
                    orderIds.add(order.getOrdNo());

                    Order saveOrder = orderRepository.findByOrdNo(order.getOrdNo()).orElseThrow();
                    String paymentKey = "test_key" + UUID.randomUUID().toString().substring(0, 7);
                    paymentkeys.add(paymentKey);
                    Payment payment = Payment.create(saveOrder, PayType.CARD, 50000);
                    payment.setPaymentKey(paymentKey, true);

                    paymentRepository.save(payment);
                }
                // flush를 통해 DB에 반영
                entityManager.flush();
                entityManager.clear();
                return null;
            });
        }
    }

    @DisplayName("100명의 사용자가 결제 실패 시 첫 번째 품목의 재고 복구에 대한 동시성 제어가 가능하다.")
    @Test
    void tossPaymentFail() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        String code = "ERR001";
        String message = "결제 실패";

        for (int i = 0; i < threadCount; i++) {
            String orderId = orderIds.get(i);
            executorService.submit(() -> {
                try {
                    paymentFacadeWithNamedLock.toFailRequest(code, message, orderId);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        int retries = 3; // 최대 재시도 횟수
        boolean success = false;

        while (retries > 0) {
            try {
                Item item = itemRepository.findById(1L).orElseThrow();
                Item item2 = itemRepository.findById(2L).orElseThrow();

                // 정합성 검증
                assertThat(item.getQty()).isEqualTo(100);
                assertThat(item2.getQty()).isEqualTo(100);

                // 검증 성공 시 루프 탈출
                success = true;
                break;
            } catch (AssertionError | NoSuchElementException e) {
                // 검증 실패 시 재시도
                retries--;
                if (retries > 0) {
                    try {
                        Thread.sleep(100); // 짧은 대기 시간
                    } catch (InterruptedException ex) {
                        throw new RuntimeException("Retry interrupted", ex);
                    }
                } else {
                    // 재시도 횟수를 초과한 경우 에러를 다시 던짐
                    throw e;
                }
            }
        }

        assertThat(success).isTrue(); // 최종적으로 성공했는지 확인
    }



    @DisplayName("100명의 사용자가 결제 취소 시 첫 번째 품목의 재고 복구에 대한 동시성 제어가 가능하다.")
    @Test
    void cancelPayment() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        String cancelReason = "사용자 요청";

        mockServer.expect(ExpectedCount.manyTimes(), MockRestRequestMatchers.anything())
                .andRespond(MockRestResponseCreators.withSuccess("{\"status\": \"success\"}", MediaType.APPLICATION_JSON));

        for (int i = 0; i < threadCount; i++) {
            String paymentKey = paymentkeys.get(i);
            executorService.submit(() -> {
                try {
                    paymentFacadeWithNamedLock.toCancelRequest(paymentKey, cancelReason);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Item item = itemRepository.findById(1L).orElseThrow();
        Item item2 = itemRepository.findById(2L).orElseThrow();

        // 정합성 검증
        assertThat(item.getQty()).isEqualTo(100);
        assertThat(item2.getQty()).isEqualTo(100);

        // Mock 서버 검증
        // mockServer.verify();
    }

}
