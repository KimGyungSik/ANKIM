package shoppingmall.ankim.domain.item.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shoppingmall.ankim.domain.order.entity.Order;

@Getter
@AllArgsConstructor
public class StockRestoreRequestedEvent {
    private final Order order;
}
