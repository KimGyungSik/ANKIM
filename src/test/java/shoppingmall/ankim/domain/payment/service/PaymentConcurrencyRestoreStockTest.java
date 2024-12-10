package shoppingmall.ankim.domain.payment.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("prod")
@TestPropertySource(properties = "spring.sql.init.mode=never")
public class PaymentConcurrencyRestoreStockTest {
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
    private PaymentFacadeWithSynchronized paymentFacadeWithSynchronized;

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
                    OrderItem orderItem = OrderItem.create(item2, 1);
                    entityManager.persist(orderItem);

                    String dynamicOrderName = "ORD20241225-" + String.format("%07d", i);

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

                    Order order = Order.create(List.of(orderItem), member, delivery, LocalDateTime.now());
                    order.setOrdCode(dynamicOrderName);
                    orderRepository.save(order);
                    orderIds.add(order.getOrdNo());

                    String paymentKey = "test_key" + String.format("%07d", i);
                    paymentkeys.add(paymentKey);
                    Payment payment = Payment.create(order, PayType.CARD, 50000);
                    payment.setPaymentKey(paymentKey,true);

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
                    paymentFacadeWithSynchronized.toFailRequestWithSynchronized(code, message, orderId);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Item item = itemRepository.findById(2L).orElseThrow();
        assertThat(item.getQty()).isEqualTo(100);
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
                    paymentFacadeWithSynchronized.toCancelRequestWithSynchronized(paymentKey, cancelReason);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Item item = itemRepository.findById(2L).orElseThrow();
        assertThat(item.getQty()).isEqualTo(100);

        // Mock 서버 검증
        // mockServer.verify();
    }

}
