package shoppingmall.ankim.domain.cart.service;

import shoppingmall.ankim.domain.cart.dto.CartItemsResponse;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.cart.service.request.AddToCartServiceRequest;

import java.util.List;

public interface CartService {
    CartItem addToCart(AddToCartServiceRequest request, String loginId); // 장바구니 상품 추가
    List<CartItemsResponse> getCartItems(String loginId); // 회원의 장바구니 상품들 불러오기
    void updateCartItemQuantity(String loginId, Long itemNo, Integer quantity);
    void deactivateSelectedItems(String loginId, List<Long> cartItemNoList);
    void deactivateOutOfStockItems(String loginId); // 품절 상품을 삭제
    Integer getCartItemCount(String loginId); // 장바구니에 담긴 상품의 개수
}
