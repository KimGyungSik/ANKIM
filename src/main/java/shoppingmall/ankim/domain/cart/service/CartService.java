package shoppingmall.ankim.domain.cart.service;

import shoppingmall.ankim.domain.cart.dto.CartItemsResponse;
import shoppingmall.ankim.domain.cart.service.request.AddToCartServiceRequest;

import java.util.List;

public interface CartService {
    void addToCart(AddToCartServiceRequest request, String accessToken); // 장바구니 상품 추가
    List<CartItemsResponse> getCartItems(String accessToken); // 회원의 장바구니 상품들 불러오기
    void updateCartItemQuantity(String accessToken, Long itemNo, Integer quantity);
    void deactivateSelectedItems(String access, List<Long> cartItemNoList);
    void deactivateOutOfStockItems(String access); // 품절 상품을 삭제
    Integer getCartItemCount(String accessToken); // 장바구니에 담긴 상품의 개수
}
