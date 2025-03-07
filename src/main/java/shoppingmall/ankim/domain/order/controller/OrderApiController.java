package shoppingmall.ankim.domain.order.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.cart.controller.request.AddToCartRequest;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.cart.service.CartService;
import shoppingmall.ankim.domain.order.dto.OrderTempResponse;
import shoppingmall.ankim.domain.order.service.OrderService;
import shoppingmall.ankim.domain.security.helper.SecurityContextHelper;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.List;

@Slf4j
@RestController
//@Controller
@RequiredArgsConstructor
@RequestMapping("api/check-out")
public class OrderApiController {

    private final OrderService orderService;
    private final CartService cartService;

    private final SecurityContextHelper securityContextHelper;

    /*
     * 임시 주문 생성 API
     * 1. 장바구니에서 선택한 상품의 cartItemNo를 받아온다.
     * 2. 전달받은 cartItemNo를 이용하여 cartItem을 조회 -> itemNo(품목번호)를 받아온다.
     **/
//    @PostMapping
//    public ApiResponse<OrderTempResponse> createTempOrder(
//            @RequestBody List<Long> cartItemNoList
//    ) {
//        String loginId = securityContextHelper.getLoginId();
//
//        OrderTempResponse tempOrder = orderService.createOrderTemp(loginId, cartItemNoList);
//        log.info("order temp total Qty : {} ", tempOrder.getTotalQty());
//        return ApiResponse.ok(tempOrder);
//    }
//
//    // 바로 구매하기
//    @PostMapping("/item")
//    public ApiResponse<OrderTempResponse> addToCartAndOrder(
//            @RequestBody AddToCartRequest request
//    ) {
//        String loginId = securityContextHelper.getLoginId();
//
//        CartItem cartItem = cartService.addToCart(request.toServiceRequest(), loginId);
//
//        OrderTempResponse tempOrder = orderService.createOrderTemp(loginId, List.of(cartItem.getNo()));
//        log.info("order temp total Qty : {} ", tempOrder.getTotalQty());
//        return ApiResponse.ok(tempOrder);
//    }
    @PostMapping
    public ApiResponse<Void> checkoutItem (
            @RequestBody List<Long> cartItemNoList,
            HttpSession session
    ) {
        securityContextHelper.getLoginId();

        // 구매하기 위해서 선택한 품목 번호 리스트 저장
        session.setAttribute("selectedCartItemList", cartItemNoList);

        return ApiResponse.ok();
    }

    @PostMapping("/one")
    public ApiResponse<Void> addToCartAndCheckout(
            @ModelAttribute AddToCartRequest request,
            HttpSession session
    ) {
        String loginId = securityContextHelper.getLoginId();

        CartItem cartItem = cartService.addToCart(request.toServiceRequest(), loginId);

        // 구매하기 위해서 선택한 품목 번호 리스트 저장
        session.setAttribute("selectedCartItemList", List.of(cartItem));

        return ApiResponse.ok();
    }
}
