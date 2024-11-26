package shoppingmall.ankim.domain.delivery.service.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeliveryCreateServiceRequest {

    private Long addressId; // 주소 ID
    private String courier; // 택배사
    private String delReq;  // 배송 요청사항

    @Builder
    public DeliveryCreateServiceRequest(Long addressId, String courier, String delReq) {
        this.addressId = addressId;
        this.courier = courier;
        this.delReq = delReq;
    }
}

