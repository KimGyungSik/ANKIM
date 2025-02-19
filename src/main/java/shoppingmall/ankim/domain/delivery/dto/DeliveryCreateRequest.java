package shoppingmall.ankim.domain.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.delivery.service.request.DeliveryCreateServiceRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryCreateRequest {

    private Long addressId; // 주소 ID
    private String courier; // 택배사
    private String delReq;  // 배송 요청사항

    public DeliveryCreateServiceRequest toServiceRequest() {
        return DeliveryCreateServiceRequest.builder()
                .addressId(this.addressId)
                .courier(this.courier)
                .delReq(this.delReq)
                .build();
    }

}
