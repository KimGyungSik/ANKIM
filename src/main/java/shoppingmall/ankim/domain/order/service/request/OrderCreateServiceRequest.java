package shoppingmall.ankim.domain.order.service.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateServiceRequest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemOrder {
        private Long itemNumber; // 품목 번호
        private Integer quantity; // 주문 수량
    }

    private List<ItemOrder> items; // 품목 번호와 수량 리스트
}