package shoppingmall.ankim.domain.payment.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import shoppingmall.ankim.domain.address.dto.MemberAddressCreateServiceRequest;
import shoppingmall.ankim.domain.delivery.repository.DeliveryRepository;
import shoppingmall.ankim.domain.delivery.service.request.DeliveryCreateServiceRequest;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.order.repository.OrderRepository;
import shoppingmall.ankim.domain.orderItem.entity.OrderItem;
import shoppingmall.ankim.domain.payment.controller.port.PaymentQueryService;
import shoppingmall.ankim.domain.payment.controller.port.PaymentService;
import shoppingmall.ankim.domain.payment.entity.PayType;
import shoppingmall.ankim.domain.payment.repository.PaymentRepository;
import shoppingmall.ankim.domain.payment.service.request.PaymentCreateServiceRequest;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.factory.MemberJwtFactory;
import shoppingmall.ankim.factory.ProductFactory;
import shoppingmall.ankim.global.config.S3Config;
import shoppingmall.ankim.global.config.TossPaymentConfig;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Transactional
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.sql.init.mode=never")
public class PaymentConcurrencyTest {
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
    private PaymentQueryService paymentQueryService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    private List<String> orderNames = new ArrayList<>();


    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        Member member = MemberJwtFactory.createMember(entityManager, "test-user@domain.com");

        Product product = ProductFactory.createProduct(entityManager);
        Item item = product.getItems().get(0);

        for (int i = 0; i < 100; i++) { // 100개의 주문 생성
            OrderItem orderItem = OrderItem.create(item, 1);
            entityManager.persist(orderItem);

            String dynamicOrderName = "ORD20241130-" + String.format("%07d", i);
            orderNames.add(dynamicOrderName); // 생성된 orderName 저장

            Order order = Order.create(List.of(orderItem), member, null, LocalDateTime.now());
            order.setOrdCode(dynamicOrderName);

            orderRepository.save(order);
        }

        entityManager.flush();
        entityManager.clear();
    }

    @DisplayName("100명의 사용자가 결제 요청 시 첫 번째 품목의 재고 차감에 대한 동시성 제어가 가능하다.")
    @Test
    @Rollback(value = false)
    void requestTossPayment() throws InterruptedException {
        // 1. 초기 상태 검증
        long orderCount = orderRepository.count();
        assertThat(orderCount).isEqualTo(100); // 100개의 주문이 생성되었는지 확인

        Item item = entityManager.createQuery("SELECT i FROM Item i WHERE i.code = :code", Item.class)
                .setParameter("code", "P001-BLK-M") // 첫 번째 품목의 코드
                .getSingleResult();

        assertThat(item.getQty()).isEqualTo(100); // 품목의 재고 수량이 100인지 확인

        // 동시성 테스트
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        DeliveryCreateServiceRequest deliveryRequest = DeliveryCreateServiceRequest.builder()
                .addressId(null)
                .courier("FastCourier")
                .delReq("문 앞에 놓아주세요")
                .build();

        MemberAddressCreateServiceRequest addressRequest = MemberAddressCreateServiceRequest.builder()
                .addressMain("서울특별시 강남구 테헤란로 123")
                .addressDetail("1층")
                .zipCode(12345)
                .phoneNumber("010-1234-5678")
                .emergencyPhoneNumber("010-5678-1234")
                .defaultAddressYn("Y")
                .build();

        for (int i = 0; i < threadCount; i++) {
            final String orderName = orderNames.get(i); // 각 스레드에서 고유한 orderName 사용
            executorService.submit(() -> {
                try {
                    PaymentCreateServiceRequest paymentRequest = PaymentCreateServiceRequest.builder()
                            .orderName(orderName) // 현재 스레드의 orderName 설정
                            .payType(PayType.CARD)
                            .amount(10000) // 결제 금액
                            .build();

                    long beforeTime = System.currentTimeMillis();
                    paymentService.requestTossPayment(paymentRequest, deliveryRequest, addressRequest);
                    System.out.println("결제 처리 시간(ms): " + (System.currentTimeMillis() - beforeTime));
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();


        // 첫 번째 품목만 가져오기
        item = entityManager.createQuery("SELECT i FROM Item i WHERE i.code = :code", Item.class)
                .setParameter("code", "P001-BLK-M") // 첫 번째 품목의 코드
                .getSingleResult();

        assertThat(item).isNotNull();

        // 재고 검증
        assertThat(item.getQty()).isEqualTo(0); // 첫 번째 품목의 재고가 정확히 0인지 확인
    }
}
