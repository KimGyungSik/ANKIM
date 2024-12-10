package shoppingmall.ankim.domain.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.payment.controller.request.PaymentCancelRequest;
import shoppingmall.ankim.domain.payment.controller.request.PaymentCreateRequestWrapper;
import shoppingmall.ankim.domain.payment.controller.request.PaymentSuccessRequest;
import shoppingmall.ankim.domain.payment.dto.PaymentCancelResponse;
import shoppingmall.ankim.domain.payment.dto.PaymentFailResponse;
import shoppingmall.ankim.domain.payment.dto.PaymentResponse;
import shoppingmall.ankim.domain.payment.dto.PaymentSuccessResponse;
import shoppingmall.ankim.domain.payment.service.PaymentFacadeWithNamedLock;
import shoppingmall.ankim.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentFacadeWithNamedLock paymentFacadeWithNamedLock;

    @PostMapping("/toss")
    public ApiResponse<PaymentResponse> requestTossPayment(@RequestBody @Valid PaymentCreateRequestWrapper requestWrapper) {
        return ApiResponse.ok(paymentFacadeWithNamedLock.createPaymentWithNamedLock(
                requestWrapper.getPaymentRequest().toServiceRequest(),
                requestWrapper.getDeliveryRequest().toServiceRequest(),
                requestWrapper.getAddressRequest().toServiceRequest()));
    }


    @PostMapping("/toss/success")
    public ApiResponse<PaymentSuccessResponse> tossPaymentSuccess(
            @RequestBody PaymentSuccessRequest paymentSuccessRequest) {
        return ApiResponse.ok(paymentFacadeWithNamedLock.toSuccessRequest(
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
        return ApiResponse.ok(paymentFacadeWithNamedLock.toFailRequest(code, message, orderId));
    }

    @PostMapping("/toss/cancel")
    public ApiResponse<PaymentCancelResponse> tossPaymentCancelPoint(
            @RequestBody  PaymentCancelRequest request
    ) {
        return ApiResponse.ok(paymentFacadeWithNamedLock.toCancelRequest(request.getPaymentKey(), request.getCancelReason()));
    }
}
