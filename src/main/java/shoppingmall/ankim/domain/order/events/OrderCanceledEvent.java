package shoppingmall.ankim.domain.order.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderCanceledEvent {
    private final String orderId;
    private final String cancelReason;
}

