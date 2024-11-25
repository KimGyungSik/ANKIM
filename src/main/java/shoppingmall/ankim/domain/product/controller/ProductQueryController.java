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
            @PathVariable("productId") Long productId) {
        return ApiResponse.ok(productRepository.findUserProductDetailResponse(productId));
    }

    // 상품 상세 by Admin
    @GetMapping("/admin/{productId}")
    public ApiResponse<ProductResponse> adminDetailProductResponse(
            @PathVariable("productId") Long productId) {
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
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "condition", required = false) Condition condition,
            @RequestParam(name = "order", required = false) OrderBy order,
            @RequestParam(name = "category", required = false) Long category,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "colorConditions", required = false) List<ColorCondition> colorConditions,
            @RequestParam(name = "priceCondition", required = false) PriceCondition priceCondition,
            @RequestParam(name = "customMinPrice", required = false) Integer customMinPrice,
            @RequestParam(name = "customMaxPrice", required = false) Integer customMaxPrice,
            @RequestParam(name = "infoSearches", required = false) List<InfoSearch> infoSearches
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.ok(
                productRepository.findUserProductListResponse(
                        pageable,
                        condition,
                        order,
                        category,
                        keyword,
                        colorConditions,
                        priceCondition,
                        customMinPrice,
                        customMaxPrice,
                        infoSearches
                )
        );
    }
}
