package shoppingmall.ankim.domain.cart.controller.v2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.cart.controller.request.AddToCartRequest;
import shoppingmall.ankim.domain.cart.dto.CartItemsResponse;
import shoppingmall.ankim.domain.cart.service.v2.V2CartService;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v2/cart")
public class V2CartApiController {

    private final V2CartService v2CartService;

    // 장바구니에 상품 담기 ( C )
    @PostMapping("/items")
    public ApiResponse<String> addToCart(
            @RequestBody AddToCartRequest request
    ) {
        String loginId = getLoginId();

        v2CartService.addToCart(request.toServiceRequest(), loginId);

        return ApiResponse.ok("장바구니에 상품이 담겼습니다.");
    }

    // 장바구니 페이지에 들어갈때 장바구니 읽어오기 ( R )
    @GetMapping
    public ApiResponse<List<CartItemsResponse>> getCartItems() {
        String loginId = getLoginId();

        List<CartItemsResponse> response = v2CartService.getCartItems(loginId);

        return ApiResponse.ok(response);
    }

    // 장바구니에 담은 상품 수량 변경하기 ( U )
    @PatchMapping("/items/{cartItemNo}")
    public ApiResponse<String> updateCartItemQuantity(
            @PathVariable Long cartItemNo,
            @RequestParam Integer quantity
    ) {
        String loginId = getLoginId();

        // service
        v2CartService.updateCartItemQuantity(loginId, cartItemNo, quantity);

        return ApiResponse.ok("장바구니 품목 수량이 변경되었습니다.");
    }

    // 장바구니에서 선택 상품 삭제하기 ( D ) -> 완전삭제X 상태만 변경
    @DeleteMapping("/items/selected")
    public ApiResponse<String> deleteSelectedItems(
            @RequestBody List<Long> cartItemNoList
    ) {
        String loginId = getLoginId();

        v2CartService.deactivateSelectedItems(loginId, cartItemNoList);
        return ApiResponse.ok("선택상품을 삭제 했습니다.");
    }

    // 장바구니에서 품절상품 삭제하기 ( D ) -> 완전삭제X 상태만 변경
    @DeleteMapping("/items/sold-out")
    public ApiResponse<String> deleteSoldOutItems(
            @CookieValue(value = "access", required = false) String access
    ) {
        String loginId = getLoginId();

        v2CartService.deactivateOutOfStockItems(access);
        return ApiResponse.ok("품절상품을 삭제 했습니다.");
    }

    // 장바구니 수 비동기로 카운팅
    @GetMapping("/count")
    public ApiResponse<Map<String, Integer>> getCartItemsCount() {
        String loginId = getLoginId();

        Integer cartItemsCount = v2CartService.getCartItemCount(loginId);

        return ApiResponse.ok(Map.of("cartItemsCount", cartItemsCount));
    }

    private static String getLoginId() {
        // SecurityContext에서 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName(); // 로그인 ID
    }
}
