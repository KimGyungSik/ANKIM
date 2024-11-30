package shoppingmall.ankim.domain.cart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.cart.controller.request.AddToCartRequest;
import shoppingmall.ankim.domain.cart.dto.CartItemsResponse;
import shoppingmall.ankim.domain.cart.service.CartService;
import shoppingmall.ankim.domain.security.exception.CookieNotIncludedException;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.List;
import java.util.Map;

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
        isExistAccessToken(access);
        cartService.addToCart(request.toServiceRequest(), access);

        return ApiResponse.ok("장바구니에 상품이 담겼습니다.");
    }

    // 장바구니 페이지에 들어갈때 장바구니 읽어오기 ( R )
    @GetMapping
    public ApiResponse<List<CartItemsResponse>> getCartItems(@CookieValue(value = "access", required = false) String access) {
        isExistAccessToken(access);

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
        isExistAccessToken(access);

        // service
        cartService.updateCartItemQuantity(access, cartItemNo, quantity);

        return ApiResponse.ok("장바구니 품목 수량이 변경되었습니다.");
    }

    // 장바구니에서 선택 상품 삭제하기 ( D ) -> 완전삭제X 상태만 변경
    @DeleteMapping("/items/selected")
    public ApiResponse<String> deleteSelectedItems(
            @RequestBody List<Long> cartItemNoList,
            @CookieValue(value = "access", required = false) String access
    ) {
        isExistAccessToken(access);

        cartService.deactivateSelectedItems(access, cartItemNoList);
        return ApiResponse.ok("선택상품을 삭제 했습니다.");
    }

    // 장바구니에서 품절상품 삭제하기 ( D ) -> 완전삭제X 상태만 변경
    @DeleteMapping("/items/sold-out")
    public ApiResponse<String> deleteSoldOutItems(
            @CookieValue(value = "access", required = false) String access
    ) {
        isExistAccessToken(access);

        cartService.deactivateOutOfStockItems(access);
        return ApiResponse.ok("품절상품을 삭제 했습니다.");
    }

    // 장바구니 수 비동기로 카운팅
    @GetMapping("/count")
    public ApiResponse<Map<String, Integer>> getCartItemsCount(@CookieValue(value = "access", required = false) String access) {
        isExistAccessToken(access);

        Integer cartItemsCount = cartService.getCartItemCount(access);

        return ApiResponse.ok(Map.of("cartItemsCount", cartItemsCount));
    }

    // 쿠키에서 access 토큰이 넘어왔는지 확인하는 것 이므로 컨트롤러 단에 유지
    private static void isExistAccessToken(String access) {
        if (access == null) {
            throw new CookieNotIncludedException(COOKIE_NOT_INCLUDED);
        }
    }
}
