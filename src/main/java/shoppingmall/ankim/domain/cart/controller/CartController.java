package shoppingmall.ankim.domain.cart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shoppingmall.ankim.domain.cart.dto.CartItemsResponse;
import shoppingmall.ankim.domain.cart.service.CartService;
import shoppingmall.ankim.domain.security.helper.SecurityContextHelper;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final SecurityContextHelper securityContextHelper;

    // 장바구니 페이지에 들어갈때 장바구니 읽어오기 ( R )
    @GetMapping
    public String getCartItems() {
        String loginId = securityContextHelper.getLoginId();

        return "/cart/cart";
    }
}
