package shoppingmall.ankim.domain.cart.service;

import shoppingmall.ankim.domain.cart.controller.request.AddToCartRequest;
import shoppingmall.ankim.domain.cart.dto.CartItemsResponse;
import shoppingmall.ankim.domain.cart.service.request.AddToCartServiceRequest;

import java.util.List;

public interface CartService {
    void addToCart(AddToCartServiceRequest request, String accessToken); // 장바구니 상품 추가
    List<CartItemsResponse> getCartItems(String accessToken); // 회원의 장바구니 상품들 불러오기
    void updateCartItemQuantity(String accessToken, Long itemNo, Integer quantity);
}
