package shoppingmall.ankim.domain.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shoppingmall.ankim.domain.payment.controller.request.PaymentCreateRequest;
import shoppingmall.ankim.domain.payment.dto.PaymentResponse;
import shoppingmall.ankim.domain.payment.service.PaymentService;
import shoppingmall.ankim.global.config.TossPaymentConfig;
import shoppingmall.ankim.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final TossPaymentConfig tossPaymentConfig;

    @PostMapping("/toss")
    public ApiResponse<PaymentResponse> requestTossPayment(@RequestBody @Valid PaymentCreateRequest request) {
        return ApiResponse.ok(paymentService.requestTossPayment(request.toServiceRequest()));
    }
}
