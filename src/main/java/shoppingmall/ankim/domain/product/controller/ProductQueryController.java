package shoppingmall.ankim.domain.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.product.dto.ProductListResponse;
import shoppingmall.ankim.domain.product.dto.ProductResponse;
import shoppingmall.ankim.domain.product.dto.ProductUserDetailResponse;
import shoppingmall.ankim.domain.product.repository.ProductRepository;
import shoppingmall.ankim.domain.product.repository.query.helper.*;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product/catalog")
public class ProductQueryController {

    private final ProductRepository productRepository;

    // 상품 상세 by User
    @GetMapping("/{productId}")
    public ApiResponse<ProductUserDetailResponse> findProductUserDetailResponse(
            @PathVariable Long productId) {
        return ApiResponse.ok(productRepository.findUserProductDetailResponse(productId));
    }

    // 상품 상세 by Admin
    @GetMapping("/admin/{productId}")
    public ApiResponse<ProductResponse> adminDetailProductResponse(
            @PathVariable Long productId) {
        return ApiResponse.ok(productRepository.findAdminProductDetailResponse(productId));
    }

    /**
     * 상품 목록 (카테고리/조건별 필터링, 조건별 정렬, 검색 통합)
     * @param page
     * @param size
     * @param condition
     * @param order
     * @param category
     * @param keyword
     * @param colorConditions
     * @param priceCondition
     * @param customMinPrice
     * @param customMaxPrice
     * @param infoSearches
     * @return
     */
    @GetMapping("/list")
    public ApiResponse<Page<ProductListResponse>> getFilteredAndSortedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Condition condition,
            @RequestParam(required = false) OrderBy order,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<ColorCondition> colorConditions,
            @RequestParam(required = false) PriceCondition priceCondition,
            @RequestParam(required = false) Integer customMinPrice,
            @RequestParam(required = false) Integer customMaxPrice,
            @RequestParam(required = false) List<InfoSearch> infoSearches
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.ok(productRepository.findUserProductListResponse(pageable, condition, order, category, keyword,
                colorConditions,priceCondition,customMinPrice,customMaxPrice,infoSearches));
    }

}
