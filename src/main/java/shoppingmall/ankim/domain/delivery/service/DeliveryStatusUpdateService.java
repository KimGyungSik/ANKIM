package shoppingmall.ankim.domain.delivery.service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import shoppingmall.ankim.domain.delivery.entity.Delivery;
import shoppingmall.ankim.domain.delivery.entity.DeliveryStatus;
import shoppingmall.ankim.domain.delivery.repository.DeliveryRepository;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.item.exception.InvalidStockQuantityException;
import shoppingmall.ankim.domain.item.exception.ItemNotFoundException;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.orderItem.entity.OrderItem;
import shoppingmall.ankim.domain.orderItem.entity.OrderStatus;
import shoppingmall.ankim.domain.orderItem.repository.OrderItemRepository;
import shoppingmall.ankim.domain.payment.controller.port.PaymentService;
import shoppingmall.ankim.domain.payment.entity.Payment;
import shoppingmall.ankim.domain.payment.exception.PaymentNotFoundException;
import shoppingmall.ankim.domain.payment.repository.PaymentRepository;
import shoppingmall.ankim.domain.payment.service.PaymentFacadeWithNamedLock;
import shoppingmall.ankim.global.config.clock.ClockHolder;
import shoppingmall.ankim.global.config.lock.LockHandler;
import shoppingmall.ankim.global.config.lock.NamedLock;
import shoppingmall.ankim.global.flag.TaskCompletionHandler;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static shoppingmall.ankim.global.exception.ErrorCode.*;

/*
- 자정(00시)마다 스케쥴링 진행
    주문 이후 1일 후에 주문 상태를 "배송중"으로 변경
    주문 이후 2일 후에 "배송완료"로 변경
    상태가 "반품요청"인 건에 대해 3일 후에 "반품 완료"로 변경
 */
@Service
@RequiredArgsConstructor
@Builder
@Slf4j
public class DeliveryStatusUpdateService {
    private final ClockHolder clockHolder;
    private final DeliveryRepository deliveryRepository;
    private final PaymentRepository paymentRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final PaymentService paymentService;
    @Async
    @SchedulerLock(name = "updateDelivery_schedulerLock", lockAtLeastFor = "PT5S", lockAtMostFor = "PT10S")
    @Scheduled(cron = "0 0 0 * * ?") // 자정(00시)마다 스케줄 실행
    public void updateDeliveryStatuses() {
        log.info("🚀 [SCHEDULED] updateDeliveryStatuses 실행 시작 - 현재 스레드: {}", Thread.currentThread().getName());

        // 현재 시간 생성 (ClockHolder 사용)
        LocalDateTime now = LocalDateTime.ofInstant(Instant.ofEpochMilli(clockHolder.millis()), ZoneId.systemDefault());
        log.info("🕒 [SCHEDULED] 현재 시간: {}", now);

        // 배송 상태 업데이트를 위한 데이터 조회
        List<Delivery> deliveries = deliveryRepository.findAllWithOrder();
        log.info("📦 [SCHEDULED] 조회된 배송 건수: {}", deliveries.size());

        for (Delivery delivery : deliveries) {
            Order order = delivery.getOrder();
            log.info("🔍 [SCHEDULED] 처리 중인 주문번호: {}, 현재 배송 상태: {}", order.getOrdNo(), delivery.getStatus());

            // 배송중(IN_PROGRESS)으로 변경 조건 확인
            if (order.getRegDate().plusDays(1).isBefore(now) &&
                    order.getOrderStatus() == OrderStatus.PAID &&
                    delivery.getStatus() == DeliveryStatus.PREPARING) {

                log.info("🚚 [SCHEDULED] 주문번호: {} 배송 상태를 '배송중'으로 변경", order.getOrdNo());
                delivery.setStatus(DeliveryStatus.IN_PROGRESS);
            }

            // 배송완료(COMPLETED)로 변경 조건 확인
            if (order.getRegDate().plusDays(2).isBefore(now) &&
                    delivery.getStatus() == DeliveryStatus.IN_PROGRESS) {

                log.info("✅ [SCHEDULED] 주문번호: {} 배송 상태를 '배송완료'로 변경", order.getOrdNo());
                delivery.setStatus(DeliveryStatus.COMPLETED);
            }

            // 반품 처리
            if (delivery.getStatus() == DeliveryStatus.RETURN_REQUESTED &&
                    order.getRegDate().plusDays(3).isBefore(now)) {

                log.info("↩️ [SCHEDULED] 주문번호: {} 반품 요청 감지 - 반품 처리 진행", order.getOrdNo());
                Payment payment = paymentRepository.findByOrderId(order.getOrdNo())
                        .orElseThrow(() -> {
                            log.error("❌ [SCHEDULED] 주문번호: {} 결제 정보 없음 - 반품 처리 실패", order.getOrdNo());
                            return new PaymentNotFoundException(PAYMENT_NOT_FOUND);
                        });

                // 재고 복구 및 결제 취소
                List<OrderItem> orderItems = orderItemRepository.findOrderItemsWithItemsByOrderNo(order.getOrdNo());
                for (OrderItem orderItem : orderItems) {
                    Item item = itemRepository.findByNo(orderItem.getItem().getNo())
                            .orElseThrow(() -> {
                                log.error("❌ [SCHEDULED] 주문번호: {} 해당 상품이 존재하지 않음 - 반품 처리 실패", order.getOrdNo());
                                return new ItemNotFoundException(ITEM_NOT_FOUND);
                            });

                    Integer quantity = orderItem.getQty();
                    if (quantity <= 0) {
                        log.error("❌ [SCHEDULED] 주문번호: {} 잘못된 재고 수량: {} - 반품 처리 실패", order.getOrdNo(), quantity);
                        throw new InvalidStockQuantityException(INVALID_STOCK_QUNTITY);
                    }
                    item.restoreQuantity(quantity);
                    log.info("🔄 [SCHEDULED] 주문번호: {} 상품 재고 복구 완료 (상품 번호: {}, 수량: {})", order.getOrdNo(), item.getNo(), quantity);
                }

                paymentService.cancelPayment(payment.getPayKey(), payment.getCancelReason());
                log.info("💳 [SCHEDULED] 주문번호: {} 결제 취소 완료", order.getOrdNo());

                order.setOrderStatus(OrderStatus.CANCELED);
                delivery.setStatus(DeliveryStatus.RETURN_COMPLETED);
                log.info("✅ [SCHEDULED] 주문번호: {} 반품 완료 - 배송 상태 변경: RETURN_COMPLETED", order.getOrdNo());
            }
        }

        log.info("✅ [SCHEDULED] updateDeliveryStatuses 실행 완료");
    }
}


