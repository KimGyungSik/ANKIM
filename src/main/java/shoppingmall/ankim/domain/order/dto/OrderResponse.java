package shoppingmall.ankim.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.delivery.dto.DeliveryResponse;
import shoppingmall.ankim.domain.item.dto.ItemResponse;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.orderItem.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private String orderNo;             // 주문 번호
    private String orderCode;         // 주문 코드
    private List<ItemResponse> items; // 주문 항목 리스트 FIXME ItemOrderResponse 정의해서 바꿔줄 것
    private DeliveryResponse delivery;          // 배송 정보
    private Integer totalQty;         // 총 주문 수량
    private Integer totalPrice;       // 총 상품 금액
    private Integer totalShipFee;     // 총 배송비
    private Integer totalDiscPrice;   // 총 할인 금액
    private Integer payAmt;           // 최종 결제 금액
    private LocalDateTime regDate;    // 주문 등록일
    private LocalDateTime modDate;    // 주문 상태 변경일
    private OrderStatus orderStatus;  // 주문 상태

    public static OrderResponse of(Order order) {
        return OrderResponse.builder()
                .orderNo(order.getOrdNo())
                .orderCode(order.getOrdCode())
                .items(order.getOrderItems().stream()
                        .map(orderItem -> ItemResponse.of(orderItem.getItem()))
                        .collect(Collectors.toList()))
                .delivery(DeliveryResponse.of(order.getDelivery()))
                .totalQty(order.getTotalQty())
                .totalPrice(order.getTotalPrice())
                .totalShipFee(order.getTotalShipFee())
                .totalDiscPrice(order.getTotalDiscPrice())
                .payAmt(order.getPayAmt())
                .regDate(order.getRegDate())
                .modDate(order.getModDate())
                .orderStatus(order.getOrderStatus())
                .build();
    }
}
