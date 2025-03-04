package shoppingmall.ankim.domain.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.cart.controller.request.AddToCartRequest;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.cart.service.CartService;
import shoppingmall.ankim.domain.order.dto.OrderResponse;
import shoppingmall.ankim.domain.order.service.OrderService;
import shoppingmall.ankim.domain.security.helper.SecurityContextHelper;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/temp-order")
public class OrderTempController {

    private final OrderService orderService;
    private final CartService cartService;

    private final SecurityContextHelper securityContextHelper;

    /*
     * 임시 주문 생성 API
     * 1. 장바구니에서 선택한 상품의 cartItemNo를 받아온다.
     * 2. 전달받은 cartItemNo를 이용하여 cartItem을 조회 -> itemNo(품목번호)를 받아온다.
     **/
    @PostMapping
    public ApiResponse<OrderResponse> createTempOrder(
            @RequestBody List<Long> cartItemNoList
    ) {
        String loginId = securityContextHelper.getLoginId();

        OrderResponse tempOrder = orderService.createTempOrder(loginId, cartItemNoList);
        return ApiResponse.ok(tempOrder);
    }


    // 바로 구매하기
    @PostMapping("/item")
    public ApiResponse<OrderResponse> addToCartAndOrder(
            @RequestBody AddToCartRequest request
    ) {
        String loginId = securityContextHelper.getLoginId();

        CartItem cartItem = cartService.addToCart(request.toServiceRequest(), loginId);

        OrderResponse tempOrder = orderService.createTempOrder(loginId, List.of(cartItem.getNo()));
        return ApiResponse.ok(tempOrder);
    }

}
