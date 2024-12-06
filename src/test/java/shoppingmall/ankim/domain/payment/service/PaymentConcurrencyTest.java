package shoppingmall.ankim.domain.payment.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;
import shoppingmall.ankim.domain.address.service.request.MemberAddressCreateServiceRequest;
import shoppingmall.ankim.domain.delivery.repository.DeliveryRepository;
import shoppingmall.ankim.domain.delivery.service.request.DeliveryCreateServiceRequest;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
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
import shoppingmall.ankim.global.dummy.InitProduct;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("prod")
@TestPropertySource(properties = "spring.sql.init.mode=never")
public class PaymentConcurrencyTest {
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
    private PaymentService paymentService;

    @Autowired
    private PaymentQueryService paymentQueryService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private ItemRepository itemRepository;

    private List<String> orderNames = new ArrayList<>();

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        transactionTemplate.execute(status -> {
            try {
                // Member와 Product 생성
                Member member = MemberJwtFactory.createMember(entityManager, "test-user@domain.com");
                Product product = ProductFactory.createProduct(entityManager);
                entityManager.persist(product);

                Item item = product.getItems().get(0);

                for (int i = 0; i < 100; i++) {
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

        for (int i = 0; i < threadCount; i++) {
            String orderName = orderNames.get(i);
            executorService.submit(() -> {
                try {
                    PaymentCreateServiceRequest paymentRequest = PaymentCreateServiceRequest.builder()
                            .orderName(orderName)
                            .payType(PayType.CARD)
                            .amount(10000)
                            .build();

                    paymentService.requestTossPayment(paymentRequest, deliveryRequest, addressRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Item item = itemRepository.findById(1L).orElseThrow();
        assertThat(item.getQty()).isEqualTo(0);
    }




}
