package shoppingmall.ankim.domain.order.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.cart.controller.request.AddToCartRequest;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.cart.service.CartService;
import shoppingmall.ankim.domain.security.helper.SecurityContextHelper;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/check-out")
public class CheckoutController {

    private final CartService cartService;

    private final SecurityContextHelper securityContextHelper;

    @PostMapping
    public ApiResponse<Void> checkoutItem (
            @RequestBody List<Long> cartItemNoList,
            HttpSession session,
            HttpServletRequest request
    ) {
        securityContextHelper.getLoginId();

        String referer = request.getHeader("referer");
        log.info("referer url : {}", referer);

        // cartItemList와 URL 정보를 하나의 Map에 저장
        Map<String, Object> checkoutData = new HashMap<>();
        checkoutData.put("cartItemList", cartItemNoList);
        checkoutData.put("referer", referer);

        // 세션에 저장
        session.setAttribute("checkoutData", checkoutData);

        return ApiResponse.ok();
    }

    @PostMapping("/products")
    public ApiResponse<Void> addToCartAndCheckout(
            @RequestBody List<AddToCartRequest> requestList,
            HttpSession session,
            HttpServletRequest request
    ) {
        String loginId = securityContextHelper.getLoginId();

        List<Long> cartItemNoList = new ArrayList<>();
        for (AddToCartRequest addToCartrequest : requestList) {
            CartItem cartItem = cartService.addToCart(addToCartrequest.toServiceRequest(), loginId);
            cartItemNoList.add(cartItem.getNo());
        }

        // 구매하기 위해서 선택한 품목 번호 리스트 저장
        String referer = request.getHeader("referer");
        log.info("referer url : {}", referer);

        // cartItemList와 URL 정보를 하나의 Map에 저장
        Map<String, Object> checkoutData = new HashMap<>();
        checkoutData.put("cartItemList", cartItemNoList);
        checkoutData.put("referer", referer);

        // 세션에 저장
        session.setAttribute("checkoutData", checkoutData);

        return ApiResponse.ok();
    }
}
