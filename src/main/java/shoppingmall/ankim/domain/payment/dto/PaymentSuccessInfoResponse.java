package shoppingmall.ankim.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.delivery.dto.DeliveryResponse;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSuccessInfoResponse {
    // 배송 총 금액
    private Integer totalShipFee;
    // DeliveryResponse
    private DeliveryResponse deliveryResponse;
}
