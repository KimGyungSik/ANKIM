package shoppingmall.ankim.domain.cart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.cart.controller.request.AddToCartRequest;
import shoppingmall.ankim.domain.cart.entity.CartItem;
import shoppingmall.ankim.domain.cart.service.CartService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartApiController {

    private final CartService cartService;

    // 장바구니에 상품 담기 ( C )
    @PostMapping("/items")
    public void addToCart(@RequestBody AddToCartRequest request,  @RequestHeader("access") String accessToken) {

    }

    // 장바구니 페이지에 들어갈때 장바구니 읽어오기 ( R )
    @GetMapping
    public void test2() {

    }

    // 장바구니에 담은 상품 수량 변경하기 ( U )
    @PatchMapping("/items/{itemNo}")
    public void test3() {

    }

    // 장바구니에서 상품 삭제하기 ( D )
    @DeleteMapping("/items/{itemNo}")
    public void test4() {

    }



}
