package shoppingmall.ankim.domain.delivery.service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class DeliveryStatusUpdateService {
    private final ClockHolder clockHolder;
    private final LockHandler lockHandler;
    private final DeliveryRepository deliveryRepository;
    private final PaymentRepository paymentRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final PaymentService paymentService;

    @Scheduled(cron = "0 0 0 * * ?") // 자정(00시)마다 스케줄 실행
    @NamedLock(key = "SCHEDULER_DELIVERY_STATUS",timeout = 30)
    public void updateDeliveryStatuses() {
            // ClockHolder를 활용하여 현재 시간 생성
            LocalDateTime now = LocalDateTime.ofInstant(Instant.ofEpochMilli(clockHolder.millis()), ZoneId.systemDefault());
            List<Delivery> deliveries = deliveryRepository.findAllWithOrder();

            for (Delivery delivery : deliveries) {
                Order order = delivery.getOrder();
                if(order.getRegDate().plusDays(1).isBefore(now) && order.getOrderStatus() == OrderStatus.PAID && delivery.getStatus() == DeliveryStatus.PREPARING) {
                    delivery.setStatus(DeliveryStatus.IN_PROGRESS); // 배송중으로 상태전환
                }
                if (order.getRegDate().plusDays(2).isBefore(now) && delivery.getStatus() == DeliveryStatus.IN_PROGRESS) {
                    delivery.setStatus(DeliveryStatus.COMPLETED); // 배송완료로 상태전환
                }

                // 반품 처리
                // 반품 요청건에 대하여 3일 후에 재고복구 및 반품 완료로 변경됨
                if(delivery.getStatus()==DeliveryStatus.RETURN_REQUESTED && order.getRegDate().plusDays(3).isBefore(now)) {
                    Payment payment = paymentRepository.findByOrderId(order.getOrdNo()).orElseThrow(() -> new PaymentNotFoundException(PAYMENT_NOT_FOUND));
                    // 재고 복구 & 결제 취소
                    List<OrderItem> orderItems = orderItemRepository.findOrderItemsWithItemsByOrderNo(order.getOrdNo());
                    for (OrderItem orderItem : orderItems) {
                        Item item = itemRepository.findByNo(orderItem.getItem().getNo())
                                .orElseThrow(()-> new ItemNotFoundException(ITEM_NOT_FOUND));
                        Integer quantity = orderItem.getQty();
                        if(quantity<=0) throw new InvalidStockQuantityException(INVALID_STOCK_QUNTITY);
                        item.restoreQuantity(quantity);
                    }
                    paymentService.cancelPayment(payment.getPayKey(),payment.getCancelReason()); // FIXME 반품 사유는 반품 요청 로직에서 setter로 추가
                    // 주문 상태 -> 결제 취소
                    order.setOrderStatus(OrderStatus.CANCELED);
                    // 반품 완료
                    delivery.setStatus(DeliveryStatus.RETURN_COMPLETED);
                }
            }
    }
}


