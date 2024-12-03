package shoppingmall.ankim.domain.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.order.dto.OrderResponse;
import shoppingmall.ankim.domain.order.service.OrderService;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/temp-order")
public class OrderTempController {

    private final OrderService orderService;

    /*
     * 임시 주문 생성 API
     * 1. 장바구니에서 선택한 상품의 cartItemNo를 받아온다.
     * 2. 전달받은 cartItemNo를 이용하여 cartItem을 조회 -> itemNo(품목번호)를 받아온다.
     **/
    @PostMapping
    public ApiResponse<OrderResponse> createTempOrder(
            @CookieValue(value = "access", required = false) String access,
            @RequestBody List<Long> cartItemNoList
    ) {

        OrderResponse tempOrder = orderService.createTempOrder(access, cartItemNoList);
        return ApiResponse.ok(tempOrder);
    }

}
