package shoppingmall.ankim.domain.order.controller;

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
import java.util.List;

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
            HttpSession session
    ) {
        securityContextHelper.getLoginId();

        // 구매하기 위해서 선택한 품목 번호 리스트 저장
        session.setAttribute("selectedCartItemList", cartItemNoList);

        return ApiResponse.ok();
    }

    @PostMapping("/products")
    public ApiResponse<Void> addToCartAndCheckout(
            @RequestBody List<AddToCartRequest> requestList,
            HttpSession session
    ) {
        String loginId = securityContextHelper.getLoginId();

        List<CartItem> cartItemList = new ArrayList<>();
        for (AddToCartRequest request : requestList) {
            cartItemList.add(cartService.addToCart(request.toServiceRequest(), loginId));
        }

        // 구매하기 위해서 선택한 품목 번호 리스트 저장
        session.setAttribute("selectedCartItemList", cartItemList);

        return ApiResponse.ok();
    }
}
