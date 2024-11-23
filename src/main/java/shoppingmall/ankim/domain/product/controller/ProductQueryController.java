package shoppingmall.ankim.domain.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shoppingmall.ankim.domain.product.dto.ProductResponse;
import shoppingmall.ankim.domain.product.dto.ProductUserDetailResponse;
import shoppingmall.ankim.domain.product.repository.ProductRepository;
import shoppingmall.ankim.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product/catalog")
public class ProductQueryController {

    private final ProductRepository productRepository;

    // by User
    @GetMapping("/{productId}")
    public ApiResponse<ProductUserDetailResponse> findProductUserDetailResponse(
            @PathVariable Long productId) {
        return ApiResponse.ok(productRepository.findUserProductDetailResponse(productId));
    }

    // by Admin
    @GetMapping("/admin/{productId}")
    public ApiResponse<ProductResponse> adminDetailProductResponse(
            @PathVariable Long productId) {
        return ApiResponse.ok(productRepository.findAdminProductDetailResponse(productId));
    }

}
