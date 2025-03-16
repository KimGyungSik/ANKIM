package shoppingmall.ankim.domain.product.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
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
import shoppingmall.ankim.domain.payment.service.PaymentFacadeWithNamedQuery;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.repository.ProductRepository;
import shoppingmall.ankim.domain.viewRolling.entity.RollingPeriod;
import shoppingmall.ankim.domain.viewRolling.entity.ViewRolling;
import shoppingmall.ankim.domain.viewRolling.repository.ViewRollingRepository;
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
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.sql.init.mode=never")
public class CurrencyIncreaseViewCountTest {
    @MockBean
    private S3Service s3Service;

    @MockBean
    private InitProduct initProduct;

    @MockBean
    private S3Config s3Config;

    @Autowired
    TossPaymentConfig tossPaymentConfig;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private ViewRollingRepository viewRollingRepository;
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        transactionTemplate.execute(status -> {
            try {
                Product product = ProductFactory.createProduct(entityManager);
                entityManager.persist(product);

                viewRollingRepository.initializeViewRolling(product.getCategory().getNo(),product.getNo());

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

    @DisplayName("10명의 사용자가 동시에 상품을 조회했을 때 조회 수는 10이다.")
    @Test
    void increaseViewCountMultiThread() throws InterruptedException {
        // given
        Product product = productRepository.findById(1L).orElseThrow();
        Long productId = product.getNo();

        int beforeViewCount = product.getViewCnt();
        int threadCount = 10; // 동시에 실행할 사용자 수

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when: 10명의 사용자가 동시에 조회 요청
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    productService.increaseViewCount(productId);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 실행될 때까지 대기

        // then: 조회수가 정확히 10 증가했는지 확인
        entityManager.clear();
        Product updatedProduct = productRepository.findById(productId).orElseThrow();
        assertThat(updatedProduct.getViewCnt()).isEqualTo(beforeViewCount + threadCount);
        ViewRolling realTimeRolling = viewRollingRepository.findByProduct_No(updatedProduct.getNo())
                .stream()
                .filter(v -> v.getPeriod() == RollingPeriod.REALTIME)
                .findFirst()
                .orElseThrow(() -> new AssertionError("REALTIME 데이터가 존재하지 않음"));

        assertThat(realTimeRolling.getTotalViews()).isEqualTo(beforeViewCount + threadCount);
    }

}
