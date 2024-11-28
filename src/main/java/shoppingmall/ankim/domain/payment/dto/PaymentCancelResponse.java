package shoppingmall.ankim.domain.payment.dto;

import lombok.Data;

import java.util.Map;

@Data
public class PaymentCancelResponse {
    private Map<String, Object> details;
}