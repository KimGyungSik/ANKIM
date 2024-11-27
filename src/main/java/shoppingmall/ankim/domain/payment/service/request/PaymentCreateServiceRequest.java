package shoppingmall.ankim.domain.payment.service.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.payment.entity.PayType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCreateServiceRequest {
    private PayType payType;
    private Integer amount;
    private String orderCode;
    private String yourSuccessUrl;
    private String yourFailUrl;
}
