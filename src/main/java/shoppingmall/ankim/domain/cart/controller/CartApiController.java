package shoppingmall.ankim.domain.cart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.cart.controller.request.AddToCartRequest;
import shoppingmall.ankim.domain.cart.dto.CartItemsResponse;
import shoppingmall.ankim.domain.cart.service.CartService;
import shoppingmall.ankim.domain.security.helper.SecurityContextHelper;
import shoppingmall.ankim.global.constants.ShippingConstants;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.List;
import java.util.Map;

import static shoppingmall.ankim.global.constants.ShippingConstants.FREE_SHIPPING_THRESHOLD;

@RestController("v1CartApiController")
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartApiController {

    private final CartService cartService;
    private final SecurityContextHelper securityContextHelper;

    // 장바구니에 상품 담기 ( C )
    @PostMapping("/items")
    public ApiResponse<String> addToCart(
            @RequestBody List<AddToCartRequest> requestList
    ) {
        String loginId = securityContextHelper.getLoginId();

        for (AddToCartRequest request : requestList) {
            cartService.addToCart(request.toServiceRequest(), loginId);
        }

        return ApiResponse.ok("장바구니에 상품이 담겼습니다.");
    }

    // 장바구니 페이지에 들어갈때 장바구니 읽어오기 ( R )
    @GetMapping
    public ApiResponse<Map<String, Object>> getCartItems() {
        String loginId = securityContextHelper.getLoginId();

        List<CartItemsResponse> response = cartService.getCartItems(loginId);

        return ApiResponse.ok(Map.of(
                "cartItems", response,
                "freeShippingThreshold", FREE_SHIPPING_THRESHOLD
        ));
    }

    // 장바구니에 담은 상품 수량 변경하기 ( U )
    @PatchMapping("/items/{cartItemNo}")
    public ApiResponse<String> updateCartItemQuantity(
            @PathVariable Long cartItemNo,
            @RequestParam Integer qty
    ) {
        String loginId = securityContextHelper.getLoginId();

        // service
        cartService.updateCartItemQuantity(loginId, cartItemNo, qty);

        return ApiResponse.ok("장바구니 품목 수량이 변경되었습니다.");
    }

    // 장바구니에서 선택 상품 삭제하기 ( D ) -> 완전삭제X 상태만 변경
    @DeleteMapping("/items/selected")
    public ApiResponse<String> deleteSelectedItems(
            @RequestBody List<Long> cartItemNoList
    ) {
        String loginId = securityContextHelper.getLoginId();

        cartService.deactivateSelectedItems(loginId, cartItemNoList);
        return ApiResponse.ok("선택상품을 삭제 했습니다.");
    }

    // 장바구니에서 품절상품 삭제하기 ( D ) -> 완전삭제X 상태만 변경
    @DeleteMapping("/items/sold-out")
    public ApiResponse<String> deleteSoldOutItems(
    ) {
        String loginId = securityContextHelper.getLoginId();

        cartService.deactivateOutOfStockItems(loginId);
        return ApiResponse.ok("품절상품을 삭제 했습니다.");
    }

    // 장바구니 수 비동기로 카운팅
    @GetMapping("/items/count")
    public ApiResponse<Map<String, Integer>> getCartItemsCount() {
        String loginId = securityContextHelper.getLoginId();

        Integer cartItemsCount = cartService.getCartItemCount(loginId);

        return ApiResponse.ok(Map.of("cartItemsCount", cartItemsCount));
    }

}
