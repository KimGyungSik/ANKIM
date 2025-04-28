package shoppingmall.ankim.domain.item.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import shoppingmall.ankim.domain.item.service.ItemService;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockRestoreRequestedEventHandler {

    private final ItemService itemService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(StockRestoreRequestedEvent event) {
        log.info("[StockRestoreRequestedEventHandler] 재고 복구 시작");

        event.getOrder().getOrderItems().forEach(orderItem -> {
            itemService.restoreStock(orderItem.getItem().getNo(), orderItem.getQty());
        });

        log.info("[StockRestoreRequestedEventHandler] 재고 복구 완료");
    }
}
