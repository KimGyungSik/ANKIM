package shoppingmall.ankim.domain.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.delivery.entity.Delivery;
import shoppingmall.ankim.domain.delivery.entity.DeliveryStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryResponse {
    private Long no;                // 배송 ID
    private String trackingNumber;  // 송장 번호
    private String courier;         // 택배사
    private DeliveryStatus status;  // 배송 상태
    private String receiver;        // 수령인 이름
    private String receiverPhone;   // 수령인 전화번호
    private String address;         // 수령인 주소
    private Integer zipcode;        // 우편번호
    private String deliveryRequest; // 배송 요청사항

    public static DeliveryResponse fromEntity(Delivery delivery) {
        return DeliveryResponse.builder()
                .no(delivery.getNo())
                .trackingNumber(delivery.getTrckNo())
                .courier(delivery.getCourier())
                .status(delivery.getStatus())
                .receiver(delivery.getReceiver())
                .receiverPhone(delivery.getReceiverPhone())
                .address(delivery.getAddress())
                .zipcode(delivery.getZipcode())
                .deliveryRequest(delivery.getDelReq())
                .build();
    }
}
