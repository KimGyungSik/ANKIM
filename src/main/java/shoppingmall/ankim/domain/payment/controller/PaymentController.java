package shoppingmall.ankim.domain.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.payment.controller.request.PaymentCancelRequest;
import shoppingmall.ankim.domain.payment.controller.request.PaymentCreateRequest;
import shoppingmall.ankim.domain.payment.controller.request.PaymentSuccessRequest;
import shoppingmall.ankim.domain.payment.dto.PaymentCancelResponse;
import shoppingmall.ankim.domain.payment.dto.PaymentFailResponse;
import shoppingmall.ankim.domain.payment.dto.PaymentResponse;
import shoppingmall.ankim.domain.payment.dto.PaymentSuccessResponse;
import shoppingmall.ankim.domain.payment.service.PaymentService;
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
    @PostMapping("/toss/success")
    public ApiResponse<PaymentSuccessResponse> tossPaymentSuccess(
            @RequestBody PaymentSuccessRequest paymentSuccessRequest) {
        return ApiResponse.ok(paymentService.tossPaymentSuccess(
                paymentSuccessRequest.getPaymentKey(),
                paymentSuccessRequest.getOrderId(),
                paymentSuccessRequest.getAmount()
        ));
    }

    @GetMapping("/toss/fail")
    public ApiResponse<PaymentFailResponse> tossPaymentFail(
            @RequestParam(value = "code") String code,
            @RequestParam(value = "message") String message,
            @RequestParam(value = "orderId") String orderId
    ) {
        return ApiResponse.ok(paymentService.tossPaymentFail(code, message, orderId));
    }

    @PostMapping("/toss/cancel")
    public ApiResponse<PaymentCancelResponse> tossPaymentCancelPoint(
            @RequestBody  PaymentCancelRequest request
    ) {
        return ApiResponse.ok(paymentService.cancelPayment(request.getPaymentKey(), request.getCancelReason()));
    }
}
