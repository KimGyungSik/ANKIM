package shoppingmall.ankim.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentFailResponse {
    String errorCode;
    String errorMessage;
    String orderId;
    String orderName;
    public static PaymentFailResponse of(String errorCode, String errorMessage, String orderId) {
        return PaymentFailResponse.builder()
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .orderId(orderId)
                .build();
    }

}
