package shoppingmall.ankim.domain.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import shoppingmall.ankim.domain.payment.controller.port.PaymentQueryService;

@RestController
@RequiredArgsConstructor
public class PaymentQueryController {
    private final PaymentQueryService paymentQueryService;
}
