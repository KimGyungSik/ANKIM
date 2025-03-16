package shoppingmall.ankim.domain.delivery.service;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;
import shoppingmall.ankim.domain.address.entity.BaseAddress;
import shoppingmall.ankim.domain.address.entity.member.MemberAddress;
import shoppingmall.ankim.domain.delivery.entity.Delivery;
import shoppingmall.ankim.domain.delivery.entity.DeliveryStatus;
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
import shoppingmall.ankim.domain.payment.controller.port.PaymentService;
import shoppingmall.ankim.domain.payment.repository.PaymentRepository;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.factory.MemberJwtFactory;
import shoppingmall.ankim.factory.ProductFactory;
import shoppingmall.ankim.global.config.S3Config;
import shoppingmall.ankim.global.config.TestClockConfig;
import shoppingmall.ankim.global.config.clock.ClockHolder;
import shoppingmall.ankim.global.config.lock.LockHandler;
import shoppingmall.ankim.global.config.track.TrackingNumberGenerator;
import shoppingmall.ankim.global.dummy.InitProduct;
import shoppingmall.ankim.global.flag.TaskCompletionHandler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("prod")
@Import(TestClockConfig.class)
@TestPropertySource(properties = "spring.sql.init.mode=never")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
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
    TaskCompletionHandler taskCompletionHandler;
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
    private LockHandler lockHandler;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private PaymentService paymentService;

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
//                        delivery.setStatus(DeliveryStatus.RETURN_REQUESTED);
                        deliveryRepository.save(delivery);

                        LocalDateTime now = LocalDateTime.ofInstant(Instant.ofEpochMilli(clockHolder.millis()), ZoneId.systemDefault());
                        Order order = Order.create(List.of(orderItem,orderItem2), member, delivery, now.minusDays(6));
                        order.setOrdCode(dynamicOrderName);
                        order.setOrderStatus(OrderStatus.PAID);
                        orderRepository.save(order);
                        orderIds.add(order.getOrdNo());

//                        String paymentKey = "test_key" + String.format("%07d", i);
//                        paymentkeys.add(paymentKey);
//                        Payment payment = Payment.create(order, PayType.CARD, 50000);
//                        payment.setPaymentKey(paymentKey, true);
//                        payment.setCancelReason("반품");
//
//                        paymentRepository.save(payment);
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

    @DisplayName("중복 작업 방지가 제대로 이루어졌는지 검증한다.")
    @Test
    void testDuplicatePreventionInScheduledTask() throws InterruptedException {
        // given
        int threadCount = 2; // 스레드 수를 2로 제한
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successfulExecutions = new AtomicInteger(0); // 실제로 작업이 수행된 횟수
        AtomicInteger skippedExecutions = new AtomicInteger(0); // 작업이 스킵된 횟수

        List<String> threadLogs = new CopyOnWriteArrayList<>(); // 각 스레드의 작업 결과 기록

        for (int i = 0; i < threadCount; i++) {
            int threadIndex = i; // 스레드 인덱스 저장
            executorService.submit(() -> {
                try {
                    log.info("Thread {} attempting task", threadIndex);

                    // 첫 번째 스레드가 작업 수행
                    if (threadIndex == 0) {
                        updateService.updateDeliveryStatuses();
                        successfulExecutions.incrementAndGet();
                        threadLogs.add(String.format("Thread %d successfully executed the task.", threadIndex));
                    } else {
                        // 두 번째 스레드는 작업 시도 후 데이터를 확인
                        updateService.updateDeliveryStatuses();

                        // 데이터 상태 확인
                        List<Delivery> deliveries = deliveryRepository.findAllWithOrder();
                        boolean isDataAlreadyUpdated = deliveries.stream()
                                .allMatch(delivery -> delivery.getStatus() == DeliveryStatus.COMPLETED);

                        if (isDataAlreadyUpdated) {
                            skippedExecutions.incrementAndGet();
                            threadLogs.add(String.format("Thread %d saw already updated data and skipped.", threadIndex));
                        } else {
                            successfulExecutions.incrementAndGet();
                            threadLogs.add(String.format("Thread %d executed the task unnecessarily.", threadIndex));
                        }
                    }
                } catch (Exception e) {
                    log.error("Unexpected error: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }


        latch.await(); // 모든 스레드의 작업 완료 대기
        executorService.shutdown();

        // then
        log.info("Successful executions: {}", successfulExecutions.get());
        log.info("Skipped executions: {}", skippedExecutions.get());
        log.info("Thread logs: {}", threadLogs);

        // 작업이 한 번만 실행되었는지 검증
        assertThat(successfulExecutions.get()).isEqualTo(1);
        assertThat(skippedExecutions.get()).isEqualTo(1);
    }

    @DisplayName("네임드 락으로 동시성 제어가 동작하는지 검증한다.")
    @Test
    void testNamedLockPreventsConcurrentExecution() throws InterruptedException {
        // given
        int threadCount = 2; // 스레드 수를 2로 제한
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger taskExecutionCount = new AtomicInteger(0);
        List<String> threadLogs = new CopyOnWriteArrayList<>();
        List<Long> lockAcquisitionTimestamps = new CopyOnWriteArrayList<>(); // 락 획득 시간 기록

        for (int i = 0; i < threadCount; i++) {
            int threadIndex = i;
            executorService.submit(() -> {
                try {
                    long startTime = System.currentTimeMillis();
                    log.info("Thread {} attempting task", threadIndex);
                    updateService.updateDeliveryStatuses(); // 작업 수행
                    long lockAcquisitionTime = System.currentTimeMillis() - startTime;

                    lockAcquisitionTimestamps.add(lockAcquisitionTime);
                    taskExecutionCount.incrementAndGet();
                    threadLogs.add(String.format("Thread %d completed task in %d ms", threadIndex, lockAcquisitionTime));
                } catch (Exception e) {
                    log.error("Unexpected error: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 작업을 완료할 때까지 대기
        executorService.shutdown();

        // then
        log.info("Task execution count: {}", taskExecutionCount.get());
        log.info("Thread logs: {}", threadLogs);

        // 락이 제대로 동작했다면, 첫 번째 스레드만 바로 작업을 수행하고 두 번째 스레드는 대기 후 수행
        assertThat(taskExecutionCount.get()).isEqualTo(2); // 두 스레드 모두 작업 수행
        assertThat(lockAcquisitionTimestamps.get(1)).isGreaterThan(lockAcquisitionTimestamps.get(0)); // 두 번째 스레드의 락 획득 시간이 더 커야 함
    }

}