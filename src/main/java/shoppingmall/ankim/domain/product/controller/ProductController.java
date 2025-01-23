package shoppingmall.ankim.domain.product.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shoppingmall.ankim.domain.product.dto.ProductListResponse;
import shoppingmall.ankim.domain.product.repository.ProductRepository;
import shoppingmall.ankim.domain.product.repository.query.helper.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductRepository productRepository;
    @GetMapping("/admin/new")
    public String productForm(Model model) {
        return "/admin/product/registerForm";
    }

    /**
     * 상품 리스트 UI 반환
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param condition 필터 조건
     * @param order 정렬 조건
     * @param category 카테고리 ID
     * @param keyword 검색 키워드
     * @param colorConditions 색상 필터 조건
     * @param priceCondition 가격 필터 조건
     * @param customMinPrice 사용자 지정 최소 가격
     * @param customMaxPrice 사용자 지정 최대 가격
     * @param infoSearches 추가 검색 조건
     * @param model 모델 객체
     * @return 상품 리스트 페이지 뷰 이름
     */
    @GetMapping("/list")
    public String getFilteredAndSortedProductList(
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
            @RequestParam(name = "infoSearches", required = false) List<InfoSearch> infoSearches,
            Model model
    ) {
        // Pageable 생성
        Pageable pageable = PageRequest.of(page, size);

        // 필터링된 상품 리스트 호출
        Page<ProductListResponse> productList = productRepository.findUserProductListResponse(
                pageable, condition, order, category, keyword, colorConditions, priceCondition,
                customMinPrice, customMaxPrice, infoSearches
        );

        // 모델에 데이터 추가
        model.addAttribute("products", productList.getContent());
        model.addAttribute("page", productList.getNumber());
        model.addAttribute("size", productList.getSize());
        model.addAttribute("totalPages", productList.getTotalPages());
        model.addAttribute("condition", condition);
        model.addAttribute("order", order);
        model.addAttribute("category", category);
        model.addAttribute("keyword", keyword);

        // 상품 리스트 페이지 반환
        return "product/list";
    }


}
