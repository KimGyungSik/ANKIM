package shoppingmall.ankim.domain.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.address.controller.request.MemberAddressCreateRequest;
import shoppingmall.ankim.domain.delivery.dto.DeliveryCreateRequest;
import shoppingmall.ankim.domain.payment.controller.request.PaymentCancelRequest;
import shoppingmall.ankim.domain.payment.controller.request.PaymentCreateRequest;
import shoppingmall.ankim.domain.payment.controller.request.PaymentCreateRequestWrapper;
import shoppingmall.ankim.domain.payment.controller.request.PaymentSuccessRequest;
import shoppingmall.ankim.domain.payment.dto.PaymentCancelResponse;
import shoppingmall.ankim.domain.payment.dto.PaymentFailResponse;
import shoppingmall.ankim.domain.payment.dto.PaymentResponse;
import shoppingmall.ankim.domain.payment.dto.PaymentSuccessResponse;
import shoppingmall.ankim.domain.payment.controller.port.PaymentService;
import shoppingmall.ankim.domain.payment.service.PaymentFacade;
import shoppingmall.ankim.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentFacade paymentFacade;

    @PostMapping("/toss")
    public ApiResponse<PaymentResponse> requestTossPayment(@RequestBody @Valid PaymentCreateRequestWrapper requestWrapper) {
        return ApiResponse.ok(paymentFacade.createPaymentWithNamedLock(
                requestWrapper.getPaymentRequest().toServiceRequest(),
                requestWrapper.getDeliveryRequest().toServiceRequest(),
                requestWrapper.getAddressRequest().toServiceRequest()));
    }


    @PostMapping("/toss/success")
    public ApiResponse<PaymentSuccessResponse> tossPaymentSuccess(
            @RequestBody PaymentSuccessRequest paymentSuccessRequest) {
        return ApiResponse.ok(paymentFacade.toSuccessRequest(
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
        return ApiResponse.ok(paymentFacade.toFailRequest(code, message, orderId));
    }

    @PostMapping("/toss/cancel")
    public ApiResponse<PaymentCancelResponse> tossPaymentCancelPoint(
            @RequestBody  PaymentCancelRequest request
    ) {
        return ApiResponse.ok(paymentFacade.toCancelRequest(request.getPaymentKey(), request.getCancelReason()));
    }
}
