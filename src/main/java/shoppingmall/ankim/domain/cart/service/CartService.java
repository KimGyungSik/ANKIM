package shoppingmall.ankim.domain.cart.service;

import shoppingmall.ankim.domain.cart.controller.request.AddToCartRequest;
import shoppingmall.ankim.domain.cart.service.request.AddToCartServiceRequest;

public interface CartService {

    void addToCart(AddToCartServiceRequest request, String accessToken);
}
