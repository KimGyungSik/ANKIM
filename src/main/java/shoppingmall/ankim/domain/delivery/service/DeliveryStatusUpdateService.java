package shoppingmall.ankim.domain.delivery.service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import shoppingmall.ankim.domain.delivery.entity.Delivery;
import shoppingmall.ankim.domain.delivery.entity.DeliveryStatus;
import shoppingmall.ankim.domain.delivery.repository.DeliveryRepository;
import shoppingmall.ankim.domain.item.service.ItemService;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.orderItem.entity.OrderItem;
import shoppingmall.ankim.domain.orderItem.entity.OrderStatus;
import shoppingmall.ankim.domain.orderItem.repository.OrderItemRepository;
import shoppingmall.ankim.domain.payment.entity.Payment;
import shoppingmall.ankim.domain.payment.exception.PaymentNotFoundException;
import shoppingmall.ankim.domain.payment.repository.PaymentRepository;
import shoppingmall.ankim.domain.payment.service.PaymentFacadeWithNamedLock;
import shoppingmall.ankim.global.config.lock.LockHandler;

import java.time.LocalDateTime;
import java.util.List;

import static shoppingmall.ankim.global.exception.ErrorCode.PAYMENT_NOT_FOUND;

/*
- 자정(00시)마다 스케쥴링 진행
    주문 이후 1일 후에 주문 상태를 "배송중"으로 변경
    주문 이후 2일 후에 "배송완료"로 변경
    상태가 "반품요청"인 건에 대해 3일 후에 "반품 완료"로 변경
 */
@Service
@RequiredArgsConstructor
public class DeliveryStatusUpdateService {
    private final PaymentFacadeWithNamedLock paymentFacadeWithNamedLock;
    private final DeliveryRepository deliveryRepository;
    private final PaymentRepository paymentRepository;
    @Scheduled(cron = "0 0 0 * * ?") // 자정(00시)마다 스케줄 실행
    @Transactional
    public void updateDeliveryStatuses(LocalDateTime now) {
        List<Delivery> deliveries = deliveryRepository.findAllWithOrder();

        for (Delivery delivery : deliveries) {
            Order order = delivery.getOrder();
            if(order.getRegDate().plusDays(1).isBefore(now) && order.getOrderStatus() == OrderStatus.PAID) {
                delivery.setStatus(DeliveryStatus.IN_PROGRESS); // 배송중으로 상태전환
            } else if (order.getRegDate().plusDays(2).isBefore(now) && delivery.getStatus() == DeliveryStatus.IN_PROGRESS) {
                delivery.setStatus(DeliveryStatus.COMPLETED); // 배송완료로 상태전환
            }

            // 반품 처리
            // 반품 요청건에 대하여 3일 후에 재고복구 및 반품 완료로 변경됨
            if(delivery.getStatus()==DeliveryStatus.RETURN_REQUESTED && order.getRegDate().plusDays(3).isBefore(now)) {
                Payment payment = paymentRepository.findByOrderId(order.getOrdNo()).orElseThrow(() -> new PaymentNotFoundException(PAYMENT_NOT_FOUND));
                // 결제 취소 & 재고 복구
                paymentFacadeWithNamedLock.toCancelRequest(payment.getPayKey(),payment.getCancelReason()); // FIXME 반품 사유는 반품 요청 로직에서 setter로 추가
                // 반품 완료
                delivery.setStatus(DeliveryStatus.RETURN_COMPLETED);
            }
        }
    }
}
