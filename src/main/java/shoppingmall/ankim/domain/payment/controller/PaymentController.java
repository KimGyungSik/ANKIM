package shoppingmall.ankim.domain.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.payment.controller.request.PaymentCreateRequest;
import shoppingmall.ankim.domain.payment.dto.PaymentFailResponse;
import shoppingmall.ankim.domain.payment.dto.PaymentResponse;
import shoppingmall.ankim.domain.payment.dto.PaymentSuccessResponse;
import shoppingmall.ankim.domain.payment.service.PaymentService;
import shoppingmall.ankim.global.config.TossPaymentConfig;
import shoppingmall.ankim.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/toss")
    public ApiResponse<PaymentResponse> requestTossPayment(@RequestBody @Valid PaymentCreateRequest request) {
        return ApiResponse.ok(paymentService.requestTossPayment(request.toServiceRequest()));
    }
    @GetMapping("/toss/success")
    public ApiResponse<PaymentSuccessResponse> tossPaymentSuccess(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Integer amount
    ) {
        return ApiResponse.ok(paymentService.tossPaymentSuccess(paymentKey, orderId, amount));
    }

    @GetMapping("/toss/fail")
    public ApiResponse<PaymentFailResponse> tossPaymentFail(
            @RequestParam String code,
            @RequestParam String message,
            @RequestParam String orderId
    ) {
        return ApiResponse.ok(paymentService.tossPaymentFail(code, message, orderId));
    }
}
