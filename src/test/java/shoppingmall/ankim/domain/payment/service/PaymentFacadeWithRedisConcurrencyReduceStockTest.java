package shoppingmall.ankim.domain.payment.service;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;
import shoppingmall.ankim.domain.address.repository.MemberAddressRepository;
import shoppingmall.ankim.domain.address.service.request.MemberAddressCreateServiceRequest;
import shoppingmall.ankim.domain.delivery.repository.DeliveryRepository;
import shoppingmall.ankim.domain.delivery.service.request.DeliveryCreateServiceRequest;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.item.exception.ShortageItemStockException;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.order.repository.OrderRepository;
import shoppingmall.ankim.domain.orderItem.entity.OrderItem;
import shoppingmall.ankim.domain.orderItem.repository.OrderItemRepository;
import shoppingmall.ankim.domain.payment.controller.port.PaymentQueryService;
import shoppingmall.ankim.domain.payment.dto.PaymentResponse;
import shoppingmall.ankim.domain.payment.entity.PayType;
import shoppingmall.ankim.domain.payment.repository.PaymentRepository;
import shoppingmall.ankim.domain.payment.service.request.PaymentCreateServiceRequest;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.repository.ProductRepository;
import shoppingmall.ankim.factory.MemberJwtFactory;
import shoppingmall.ankim.factory.ProductFactory;
import shoppingmall.ankim.global.config.S3Config;
import shoppingmall.ankim.global.config.TossPaymentConfig;
import shoppingmall.ankim.global.dummy.InitProduct;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("prod")
@TestPropertySource(properties = "spring.sql.init.mode=never")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PaymentFacadeWithRedisConcurrencyReduceStockTest {
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
    private PaymentFacadeWithRedis paymentFacadeWithRedis;

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

    @Autowired
    private RedissonClient redissonClient;

    private MockRestServiceServer mockServer;

    private List<String> orderNames = new ArrayList<>();
    private List<String> orderIds = new ArrayList<>();
    private List<String> paymentkeys = new ArrayList<>();

    @BeforeAll
    void setUp() throws Exception{
        if (!orderRepository.existsByOrdCode("ORD20241130-0000000")) { // 데이터가 이미 존재하는지 확인
            mockServer = MockRestServiceServer.createServer(restTemplate);
            transactionTemplate.execute(status -> {
                try {
                    // Member와 Product 생성
                    Member member = MemberJwtFactory.createMember(entityManager, "test-user@domain.com");
                    Product product = ProductFactory.createProduct(entityManager);
                    entityManager.persist(product);

                    Item item = product.getItems().get(0);
                    Item item2 = product.getItems().get(1);

                    // 결제 요청에 대한 더미 (재고 감소)
                    for (int i = 0; i < 150; i++) {
                        OrderItem orderItem = OrderItem.create(item, 1);
                        OrderItem orderItem2 = OrderItem.create(item2, 1);
                        entityManager.persist(orderItem);
                        entityManager.persist(orderItem2);

                        String dynamicOrderName = "ORD20241130-" + String.format("%07d", i);
                        orderNames.add(dynamicOrderName);

                        Order order = Order.create(List.of(orderItem, orderItem2), member, null, LocalDateTime.now());
                        order.setOrdCode(dynamicOrderName);

                        orderRepository.save(order);
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

                    String dynamicOrderName = "ORD20251132-" + UUID.randomUUID().toString().substring(0, 6);
                    orderNames.add(dynamicOrderName);

                    Order order = Order.create(List.of(orderItem, orderItem2), member, null, LocalDateTime.now());
                    order.setOrdCode(dynamicOrderName);

                    orderRepository.save(order);
                }
                // flush를 통해 DB에 반영
                entityManager.flush();
                entityManager.clear();
                return null;
            });
        }
    }

    @DisplayName("100명의 사용자가 결제 요청 시 첫 번째 품목의 재고 차감에 대한 동시성 제어가 가능하다.")
    @Test
    void requestTossPayment() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

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

        RCountDownLatch startLatch = redissonClient.getCountDownLatch("startLatch");
        startLatch.trySetCount(2); // 두 서버가 대기하도록 설정

        RCountDownLatch doneLatch = redissonClient.getCountDownLatch("doneLatch");
        doneLatch.trySetCount(2); // 두 서버가 완료 신호를 기다리도록 설정

        // 100개 요청을 미리 준비
        for (int i = 0; i < threadCount; i++) {
            String orderName = orderNames.get(i);
            executorService.submit(() -> {
                try {
                    // 작업 시작 로그
                    log.info("Thread {} started at {}", Thread.currentThread().getId(), System.currentTimeMillis());
                    // 작업 시작 전 동기화
                    startLatch.await();

                    PaymentCreateServiceRequest paymentRequest = PaymentCreateServiceRequest.builder()
                            .orderName(orderName)
                            .payType(PayType.CARD)
                            .amount(10000)
                            .build();

//                    PaymentResponse paymentWithSynchronized = paymentFacadeWithRedis.createPaymentWithRedis(paymentRequest, deliveryRequest, addressRequest);
                    // 작업 종료 로그
                    log.info("Thread {} finished at {}", Thread.currentThread().getId(), System.currentTimeMillis());
                    latch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 두 서버 간 작업 시작 신호 보내기
        startLatch.countDown(); // 첫 번째 서버 신호
        startLatch.countDown(); // 두 번째 서버 신호

        latch.await(); // 현재 서버의 작업 완료 대기
        doneLatch.countDown(); // 작업 완료 신호 전송

        // 작업 결과 검증
        Item item = itemRepository.findById(1L).orElseThrow();
        Item item2 = itemRepository.findById(2L).orElseThrow();
        assertThat(item.getQty()).isEqualTo(0);
        assertThat(item2.getQty()).isEqualTo(0);
    }


    @Test
    @DisplayName("재고가 100개인 상품을 101개의 스레드가 1개씩 동시에 구매했을 때 하나의 구매가 실패한다.")
    void testConcurrentBuyProduct() throws Exception {
        int threadCount = 101;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

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

        // 예외 발생 카운트
        List<Exception> exceptions = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            String orderName = orderNames.get(i);
            executorService.submit(() -> {
                try {
                    PaymentCreateServiceRequest paymentRequest = PaymentCreateServiceRequest.builder()
                            .orderName(orderName)
                            .payType(PayType.CARD)
                            .amount(10000)
                            .build();

//                    paymentFacadeWithRedis.createPaymentWithRedis(paymentRequest, deliveryRequest, addressRequest);
                } catch (Exception e) {
                    synchronized (exceptions) {
                        exceptions.add(e);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // 재고 검증
        Item item = itemRepository.findById(1L).orElseThrow();
        assertThat(item.getQty()).isEqualTo(0);

        // 예외 검증 (1개의 구매 실패가 발생해야 함)
        assertThat(exceptions.size()).isEqualTo(1);
        assertThat(exceptions.get(0))
                .isInstanceOf(ShortageItemStockException.class)
                .hasMessage("차감할 재고 수량이 없습니다.");
    }
}
