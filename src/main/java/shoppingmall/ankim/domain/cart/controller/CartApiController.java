package shoppingmall.ankim.domain.cart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.cart.controller.request.AddToCartRequest;
import shoppingmall.ankim.domain.cart.dto.CartItemsResponse;
import shoppingmall.ankim.domain.cart.service.CartService;
import shoppingmall.ankim.domain.security.exception.CookieNotIncludedException;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.List;

import static shoppingmall.ankim.global.exception.ErrorCode.COOKIE_NOT_INCLUDED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartApiController {

    private final CartService cartService;

    // 장바구니에 상품 담기 ( C )
    @PostMapping("/items")
    public ApiResponse<String> addToCart(
            @RequestBody AddToCartRequest request
           ,@CookieValue(value = "access", required = false) String access // Cookie에서 access 토큰 가져오기
    ) {
        if (access == null) {
            throw new CookieNotIncludedException(COOKIE_NOT_INCLUDED);
        }
        cartService.addToCart(request.toServiceRequest(), access);

        return ApiResponse.ok("장바구니에 상품이 담겼습니다.");
    }

    // 장바구니 페이지에 들어갈때 장바구니 읽어오기 ( R )
    @GetMapping
    public ApiResponse<List<CartItemsResponse>> getCartItems(@CookieValue(value = "access", required = false) String access) {
        if (access == null) {
            throw new CookieNotIncludedException(COOKIE_NOT_INCLUDED);
        }

        List<CartItemsResponse> response = cartService.getCartItems(access);

        return ApiResponse.ok(response);
    }

    // 장바구니에 담은 상품 수량 변경하기 ( U )
    @PatchMapping("/items/{cartItemNo}")
    public ApiResponse<String> updateCartItemQuantity(
            @PathVariable Long cartItemNo,
            @RequestParam Integer quantity,
            @CookieValue(value = "access", required = false) String access
    ) {
        if (access == null) {
            throw new CookieNotIncludedException(COOKIE_NOT_INCLUDED);
        }

        // service
        cartService.updateCartItemQuantity(access, cartItemNo, quantity);

        return ApiResponse.ok("장바구니 품목 수량이 변경되었습니다.");
    }

    // 장바구니에서 상품 삭제하기 ( D ) - 선택 삭제
    @DeleteMapping("/items/selected")
    public ApiResponse<String> deleteSelectedItems(
            @CookieValue(value = "access", required = false) String access
    ) {
        if (access == null) {
            throw new CookieNotIncludedException(COOKIE_NOT_INCLUDED);
        }

        return ApiResponse.ok("");
    }

    // 장바구니에서 상품 삭제하기 ( D ) - 선택 삭제
    @DeleteMapping("/items/sole-out")
    public ApiResponse<String> deleteSoldOutItems(
            @CookieValue(value = "access", required = false) String access
    ) {
        if (access == null) {
            throw new CookieNotIncludedException(COOKIE_NOT_INCLUDED);
        }

        cartService.deactivateOutOfStockItems(access);
        return ApiResponse.ok("품절상품을 삭제 했습니다.");
    }



}
