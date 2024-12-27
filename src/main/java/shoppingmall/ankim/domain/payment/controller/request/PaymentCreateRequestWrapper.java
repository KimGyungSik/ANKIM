package shoppingmall.ankim.domain.payment.controller.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.address.controller.request.MemberAddressCreateRequest;
import shoppingmall.ankim.domain.delivery.dto.DeliveryCreateRequest;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCreateRequestWrapper {
    @Valid
    @NotNull
    private PaymentCreateRequest paymentRequest;

    @Valid
    @NotNull
    private DeliveryCreateRequest deliveryRequest;

    @Valid
    @NotNull
    private MemberAddressCreateRequest addressRequest;
}

