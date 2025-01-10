package shoppingmall.ankim.domain.cart.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.cart.dto.CartItemsResponse;
import shoppingmall.ankim.domain.cart.service.CartService;
import shoppingmall.ankim.domain.security.helper.SecurityContextHelper;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final SecurityContextHelper securityContextHelper;

    // 장바구니 페이지
    @GetMapping
    public String getCartItems() {
        log.info("장바구니 페이지 진입");
        try {
            securityContextHelper.getLoginId();
        } catch (IllegalStateException e) {
            log.error("로그인이 필요합니다.");
            return "redirect:/login/member";
        }

        return "cart/cart";
    }
}
