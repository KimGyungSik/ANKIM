package shoppingmall.ankim.domain.delivery.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import net.javacrumbs.shedlock.core.LockProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.web.client.RestTemplate;
import shoppingmall.ankim.domain.delivery.entity.DeliveryStatus;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.order.repository.OrderRepository;
import shoppingmall.ankim.domain.orderItem.entity.OrderItem;
import shoppingmall.ankim.domain.orderItem.entity.OrderStatus;
import shoppingmall.ankim.domain.orderItem.repository.OrderItemRepository;
import shoppingmall.ankim.domain.payment.entity.PayType;
import shoppingmall.ankim.domain.payment.entity.Payment;
import shoppingmall.ankim.domain.payment.repository.PaymentRepository;
import shoppingmall.ankim.factory.OrderFactory;
import shoppingmall.ankim.global.config.S3Config;
import shoppingmall.ankim.global.config.SchedulerConfig;
import shoppingmall.ankim.global.config.TestAsyncConfig;
import shoppingmall.ankim.global.config.TestClockConfig;
import shoppingmall.ankim.global.config.clock.ClockHolder;
import shoppingmall.ankim.global.dummy.InitProduct;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import({TestClockConfig.class, SchedulerConfig.class, TestAsyncConfig.class})
@ActiveProfiles("prod")
@Transactional
@TestPropertySource(properties = "spring.sql.init.mode=never")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DeliveryStatusUpdateServiceTest {
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
    private PaymentRepository paymentRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ClockHolder clockHolder;
    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private DeliveryStatusUpdateService updateService;

    @DisplayName("주문 이후 1일 후에 배송 상태를 '배송중'으로 변경할 수 있다.")
    @Test
    void updateDeliveryStatusesWithIN_PROGRESS() {
        // given
        LocalDateTime now = LocalDateTime.ofInstant(Instant.ofEpochMilli(clockHolder.millis()), ZoneId.systemDefault());
        String orderCode = "ORD20241125-1234567";

        // Order와 Payment 데이터 생성 및 저장
        Order order = OrderFactory.createOrderWithDeliveryAndLocalDateTime(entityManager,now.minusDays(2));
        order.setOrdCode(orderCode);
        order.setOrderStatus(OrderStatus.PAID);
        orderRepository.save(order);

        // when
        updateService.updateDeliveryStatuses();

        // then
        Order ored = orderRepository.findByOrdNo(order.getOrdNo()).orElseThrow();
        assertThat(ored.getDelivery().getStatus()).isEqualTo(DeliveryStatus.IN_PROGRESS);
    }

    @DisplayName("주문 이후 2일 후에 배송 상태를 '배송 완료'로 변경할 수 있다.")
    @Test
    void updateDeliveryStatusesWithCOMPLETED() {
        // given
        LocalDateTime now = LocalDateTime.ofInstant(Instant.ofEpochMilli(clockHolder.millis()), ZoneId.systemDefault());
        String orderCode = "ORD20241125-1234567";

        // Order와 Payment 데이터 생성 및 저장
        Order order = OrderFactory.createOrderWithDeliveryAndLocalDateTime(entityManager,now.minusDays(3));
        order.setOrdCode(orderCode);
        order.setOrderStatus(OrderStatus.PAID);
        order.getDelivery().setStatus(DeliveryStatus.IN_PROGRESS);
        orderRepository.save(order);

        // when
        updateService.updateDeliveryStatuses();

        // then
        Order ored = orderRepository.findByOrdNo(order.getOrdNo()).orElseThrow();
        assertThat(ored.getDelivery().getStatus()).isEqualTo(DeliveryStatus.COMPLETED);
    }

    @DisplayName("반품 요청건에 대하여 3일 후에 재고복구 및 결제 취소를 하게되며 배송 상태는 '반품 완료'로 변경할 수 있다. ")
    @Test
    void updateDeliveryStatusesWithRETURN_COMPLETED() {
        // Mock RestTemplate 동작 정의
        Mockito.when(restTemplate.postForObject(
                Mockito.anyString(),
                Mockito.any(),
                Mockito.eq(String.class)
        )).thenReturn("{\"status\": \"success\"}");

        LocalDateTime now = LocalDateTime.ofInstant(Instant.ofEpochMilli(clockHolder.millis()), ZoneId.systemDefault());
        String orderCode = "ORD20241125-1234567";

        // Order와 Payment 데이터 생성 및 저장
        Order order = OrderFactory.createOrderWithDeliveryAndLocalDateTime(entityManager, now.minusDays(4));
        order.setOrdCode(orderCode);
        order.setOrderStatus(OrderStatus.PAID);
        order.getDelivery().setStatus(DeliveryStatus.RETURN_REQUESTED);
        orderRepository.save(order);

        String paymentKey = "test_key";
        Payment payment = Payment.create(order, PayType.CARD, 50000);
        payment.setPaymentKey(paymentKey, true);
        payment.setCancelReason("반품");
        paymentRepository.save(payment);

        // when
        updateService.updateDeliveryStatuses();

        // then
        Order ored = orderRepository.findByOrdNo(order.getOrdNo()).orElseThrow();
        // 재고 확인
        List<OrderItem> orderItems = orderItemRepository.findOrderItemsWithItemsByOrderNo(ored.getOrdNo());
        assertThat(orderItems.get(0).getItem().getQty()).isEqualTo(2);
        assertThat(orderItems.get(1).getItem().getQty()).isEqualTo(103);

        // 결제 취소 확인
        assertThat(paymentRepository.findByOrderId(ored.getOrdNo()).get().getCancelReason()).isEqualTo("반품");
        assertThat(ored.getOrderStatus()).isEqualTo(OrderStatus.CANCELED);
        // 배송 상태 확인 -> 반품 완료
        assertThat(ored.getDelivery().getStatus()).isEqualTo(DeliveryStatus.RETURN_COMPLETED);
    }

}