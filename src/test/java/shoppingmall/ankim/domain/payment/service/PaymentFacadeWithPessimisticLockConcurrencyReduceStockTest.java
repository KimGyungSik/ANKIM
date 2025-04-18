package shoppingmall.ankim.domain.payment.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("prod")
@TestPropertySource(properties = "spring.sql.init.mode=never")
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 테스트 인스턴스를 클래스 수준으로 변경
public class PaymentFacadeWithPessimisticLockConcurrencyReduceStockTest {
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
    private PaymentFacadeWithPessimisticLock paymentFacadeWithPessimisticLock;

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

    @BeforeAll
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

                // 결제 요청에 대한 더미 (재고 감소)
                for (int i = 0; i < 150; i++) {
                    OrderItem orderItem = OrderItem.create(item, 1);
                    entityManager.persist(orderItem);

                    String dynamicOrderName = "ORD20241130-" + String.format("%07d", i);
                    orderNames.add(dynamicOrderName);

                    Order order = Order.create(List.of(orderItem), member, null, LocalDateTime.now());
                    order.setOrdCode(dynamicOrderName);

                    orderRepository.save(order);
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

    @Test
    @DisplayName("100명의 사용자가 결제 요청 시 첫 번째 품목의 재고 차감에 대한 동시성 제어가 가능하다.")
    void requestTossPayment() throws InterruptedException {
        int threadCount = 100; // 실행할 스레드 수
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger failureCount = new AtomicInteger(); // 실패한 트랜잭션 수를 기록

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

        long startTime = System.currentTimeMillis(); // 실행 시작 시간

        for (int i = 0; i < threadCount; i++) {
            String orderName = orderNames.get(i);
            executorService.submit(() -> {
                try {
                    PaymentCreateServiceRequest paymentRequest = PaymentCreateServiceRequest.builder()
                            .orderName(orderName)
                            .payType(PayType.CARD)
                            .amount(10000)
                            .build();

                    paymentFacadeWithPessimisticLock.createPaymentWithPessimisticLock(paymentRequest, deliveryRequest, addressRequest);
                } catch (Exception e) {
                    failureCount.incrementAndGet(); // 실패 시 카운트 증가
                    e.printStackTrace();
                } finally {
                    latch.countDown(); // 현재 스레드 작업 완료
                }
            });
        }

        latch.await(); // 모든 스레드 종료 대기
        long endTime = System.currentTimeMillis(); // 실행 종료 시간

        System.out.println("Execution Time: " + (endTime - startTime) + " ms"); // 실행 시간 출력
        System.out.println("Failed Transactions: " + failureCount.get()); // 실패한 트랜잭션 수 출력

        // 결과 검증: 첫 번째 품목의 재고가 0이어야 함
        Item item = itemRepository.findById(1L).orElseThrow();
        assertThat(item.getQty()).isEqualTo(0);
    }


    @Test
    @DisplayName("재고가 100개인 상품을 101개의 스레드가 1개씩 동시에 구매했을 때 1개의 주문에 대한 재고 부족 예외가 발생한다.")
    void testConcurrentBuyProduct() throws Exception {
        int threadCount = 101;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger failureCount = new AtomicInteger(); // 실패한 트랜잭션 수를 기록

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

                    paymentFacadeWithPessimisticLock.createPaymentWithPessimisticLock(paymentRequest, deliveryRequest, addressRequest);
                } catch (Exception e) {
                    failureCount.incrementAndGet(); // 실패 시 카운트 증가
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

        System.out.println("Failed Transactions: " + failureCount.get()); // 실패한 트랜잭션 수 출력
        assertThat(failureCount.get()).isEqualTo(1);

        // 예외 검증 (1개의 구매 실패가 발생해야 함)
        assertThat(exceptions.size()).isEqualTo(1);
        assertThat(exceptions.get(0))
                .isInstanceOf(ShortageItemStockException.class)
                .hasMessage("차감할 재고 수량이 없습니다.");
    }

}
