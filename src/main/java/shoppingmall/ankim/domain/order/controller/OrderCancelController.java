package shoppingmall.ankim.domain.order.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shoppingmall.ankim.domain.order.service.OrderService;
import shoppingmall.ankim.global.response.ApiResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/order/cancel")
public class OrderCancelController {

    private final OrderService orderService;

    @GetMapping
    public ApiResponse<Void> orderCancel(String orderId, String cancelReason) {
        orderService.cancel(orderId,cancelReason);
        return ApiResponse.ok();
    }

}
