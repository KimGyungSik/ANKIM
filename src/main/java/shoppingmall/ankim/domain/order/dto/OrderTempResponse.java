package shoppingmall.ankim.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.address.dto.ExistAddressResponse;
import shoppingmall.ankim.domain.address.entity.member.MemberAddress;
import shoppingmall.ankim.domain.item.dto.ItemResponse;
import shoppingmall.ankim.domain.order.entity.Order;
import shoppingmall.ankim.domain.orderItem.dto.OrderItemResponse;
import shoppingmall.ankim.domain.orderItem.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderTempResponse {
    private String orderNo;           // 주문 번호
    private String orderCode;         // 주문 코드
    private List<OrderItemResponse> items; // 주문 항목 리스트
    private List<ExistAddressResponse> addresses;
    private Integer totalQty;         // 총 주문 수량
    private Integer totalPrice;       // 총 상품 금액
    private Integer totalShipFee;     // 총 배송비
    private Integer totalDiscPrice;   // 총 할인 금액
    private Integer payAmt;           // 최종 결제 금액

    public static OrderTempResponse tempOf(Order order) {
        return OrderTempResponse.builder()
                .orderNo(order.getOrdNo())
                .orderCode(order.getOrdCode())
                .items(order.getOrderItems().stream()
                        .map(OrderItemResponse::of)
                        .collect(Collectors.toList()))
                .totalQty(order.getTotalQty())
                .totalPrice(order.getTotalPrice())
                .totalShipFee(order.getTotalShipFee())
                .totalDiscPrice(order.getTotalDiscPrice())
                .payAmt(order.getPayAmt())
                .build();
    }

    // 메서드 체이닝으로 주소 추가할 수 있도록
    public OrderTempResponse withAddresses(List<MemberAddress> memberAddresses) {
        this.addresses = memberAddresses.stream()
                .map(ExistAddressResponse::of)
                .collect(Collectors.toList());
        return this;
    }
}
