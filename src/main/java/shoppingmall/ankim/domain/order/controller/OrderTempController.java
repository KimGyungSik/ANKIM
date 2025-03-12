package shoppingmall.ankim.domain.order.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.cart.controller.request.AddToCartRequest;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.cart.exception.CartItemNotSellingException;
import shoppingmall.ankim.domain.cart.service.CartService;
import shoppingmall.ankim.domain.order.dto.OrderTempErrorResponse;
import shoppingmall.ankim.domain.order.dto.OrderTempResponse;
import shoppingmall.ankim.domain.order.exception.OrderTempException;
import shoppingmall.ankim.domain.order.service.OrderService;
import shoppingmall.ankim.domain.security.helper.SecurityContextHelper;
import shoppingmall.ankim.global.exception.ErrorCode;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.List;
import java.util.Map;

import static shoppingmall.ankim.global.exception.ErrorCode.CART_ITEM_NOT_SELECTED;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/temp-order")
public class OrderTempController {

    private final OrderService orderService;

    private final SecurityContextHelper securityContextHelper;

    /*
     * 임시 주문 생성 API
     * 1. 장바구니에서 선택한 상품의 cartItemNo를 세션에서 꺼내온다.
     * 2. 세션에 정보가 없는 경우 예외를 발생시킨다.
     * 3. 전달받은 cartItemNo를 이용하여 cartItem을 조회 -> itemNo(품목번호)를 받아온다.
     **/
    @GetMapping
    public ApiResponse<?> createTempOrder(HttpSession session) {
        String loginId = securityContextHelper.getLoginId();

        // 세션에서 checkoutData Map을 꺼냄
        Map<String, Object> checkoutData = (Map<String, Object>) session.getAttribute("checkoutData");
        String referer = checkoutData != null ? (String) checkoutData.get("referer") : null;

        if (checkoutData == null) {
            throw new OrderTempException(CART_ITEM_NOT_SELECTED, referer);
        }

        // checkoutData에서 cartItemList를 꺼냄
        List<Long> cartItemNoList = (List<Long>) checkoutData.get("cartItemList");
        if (cartItemNoList == null || cartItemNoList.isEmpty()) {
            throw new OrderTempException(CART_ITEM_NOT_SELECTED, referer);
        }

        OrderTempResponse tempOrder = orderService.createOrderTemp(loginId, cartItemNoList, referer);

        return ApiResponse.ok(tempOrder);
    }
}
