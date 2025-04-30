package shoppingmall.ankim.domain.item.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import shoppingmall.ankim.domain.item.exception.StockReduceFailedException;
import shoppingmall.ankim.domain.item.service.ItemService;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.order.repository.OrderRepository;
import shoppingmall.ankim.global.exception.ErrorCode;

import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockReduceRequestedEventHandler {

    private final ItemService itemService;
    @EventListener
    public void handle(StockReduceRequestedEvent event) {
        Order order = event.getOrder();

        try {
            log.info("[StockReduceRequestedEventHandler] 재고 차감 시작");

            order.getOrderItems().forEach(orderItem -> {
                itemService.reduceStock(orderItem.getItem().getNo(), orderItem.getQty());
            });

        } catch (Exception e) {
            log.error("[StockReduceRequestedEventHandler] 재고 차감 실패", e);
            throw new StockReduceFailedException(STOCK_REDUCE_FAILED, order.getOrdCode());
        }
    }
}
