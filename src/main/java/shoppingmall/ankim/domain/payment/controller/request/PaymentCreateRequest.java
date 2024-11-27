package shoppingmall.ankim.domain.payment.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.payment.entity.PayType;
import shoppingmall.ankim.domain.payment.entity.Payment;
import shoppingmall.ankim.domain.payment.service.request.PaymentCreateServiceRequest;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCreateRequest {
    @NotNull(message = "결제 타입은 필수 입력값 입니다.")
    private PayType payType;
    @NotNull(message = "결제 금액은 필수 입력값 입니다.")
    private Long amount;
    @NotBlank(message = "주문 코드는 필수 입력값 입니다.")
    private String orderCode;

    private String yourSuccessUrl;
    private String yourFailUrl;

    public PaymentCreateServiceRequest toServiceRequest() {
        return PaymentCreateServiceRequest.builder()
                .payType(this.payType)
                .amount(this.amount)
                .orderCode(this.orderCode)
                .yourSuccessUrl(this.yourSuccessUrl)
                .yourFailUrl(this.yourFailUrl)
                .build();
    }
}
