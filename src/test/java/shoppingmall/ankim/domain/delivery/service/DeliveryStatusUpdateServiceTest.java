package shoppingmall.ankim.domain.delivery.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import shoppingmall.ankim.domain.delivery.entity.DeliveryStatus;
import shoppingmall.ankim.domain.delivery.repository.DeliveryRepository;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.order.repository.OrderRepository;
import shoppingmall.ankim.domain.orderItem.entity.OrderStatus;
import shoppingmall.ankim.domain.payment.entity.PayType;
import shoppingmall.ankim.domain.payment.entity.Payment;
import shoppingmall.ankim.domain.payment.repository.PaymentRepository;
import shoppingmall.ankim.factory.OrderFactory;
import shoppingmall.ankim.global.config.S3Config;
import shoppingmall.ankim.global.config.TestClockConfig;
import shoppingmall.ankim.global.config.TestClockHolder;
import shoppingmall.ankim.global.config.clock.ClockHolder;
import shoppingmall.ankim.global.dummy.InitProduct;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestClockConfig.class)
@ActiveProfiles("test")
@Transactional
@TestPropertySource(properties = "spring.sql.init.mode=never")
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
    private ClockHolder clockHolder;

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

}