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
@RequestMapping("/toss")
public class TossPaymentController {
    private final PaymentFacadeWithNamedLock paymentFacadeWithNamedLock;

    @GetMapping("/success")
    public String success(
            @RequestParam(value = "code") String code,
            @RequestParam(value = "message") String message,
            @RequestParam(value = "orderId") String orderId) {
        return "/payment/success";
    }


    @GetMapping("/fail")
    public String tossPaymentSuccess(
            @RequestParam(value = "code") String code,
            @RequestParam(value = "message") String message,
            @RequestParam(value = "orderId") String orderId) {
        paymentFacadeWithNamedLock.toFailRequest(code, message, orderId);
        return "/payment/fail";
    }
}
