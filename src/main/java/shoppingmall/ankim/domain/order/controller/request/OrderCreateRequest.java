package shoppingmall.ankim.domain.order.controller.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.order.service.request.OrderCreateServiceRequest;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemOrder {
        private Long itemNumber; // 품목 번호
        private Integer quantity; // 주문 수량
    }

    @NotEmpty(message = "품목 번호 리스트는 필수입니다.")
    private List<ItemOrder> items; // 품목 번호와 수량 리스트

    public OrderCreateServiceRequest toServiceRequest() {
        List<OrderCreateServiceRequest.ItemOrder> serviceItems = this.items.stream()
                .map(item -> new OrderCreateServiceRequest.ItemOrder(item.getItemNumber(), item.getQuantity()))
                .collect(Collectors.toList());

        return new OrderCreateServiceRequest(serviceItems);
    }
}
