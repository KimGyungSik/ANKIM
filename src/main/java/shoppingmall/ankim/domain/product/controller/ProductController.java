package shoppingmall.ankim.domain.product.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shoppingmall.ankim.domain.category.service.query.CategoryQueryService;
import shoppingmall.ankim.domain.item.dto.ItemResponse;
import shoppingmall.ankim.domain.option.dto.OptionGroupResponse;
import shoppingmall.ankim.domain.option.dto.OptionValueResponse;
import shoppingmall.ankim.domain.product.dto.ProductListResponse;
import shoppingmall.ankim.domain.product.dto.ProductResponse;
import shoppingmall.ankim.domain.product.repository.ProductRepository;
import shoppingmall.ankim.domain.product.repository.query.helper.*;
import shoppingmall.ankim.domain.product.service.ProductService;
import shoppingmall.ankim.domain.searchLog.service.SearchLogService;
import shoppingmall.ankim.domain.viewRolling.entity.RollingPeriod;
import shoppingmall.ankim.domain.viewRolling.repository.ViewRollingRepository;
import shoppingmall.ankim.global.response.ApiResponse;
import java.util.*;
import java.util.stream.Collectors;

import static shoppingmall.ankim.domain.viewRolling.entity.RollingPeriod.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductRepository productRepository;
    private final ViewRollingRepository viewRollingRepository;
    private final ProductService productService;
    private final CategoryQueryService categoryQueryService;
    private final SearchLogService searchLogService;
    @GetMapping("/admin/new")
    public String productForm(Model model) {
        return "admin/product/registerForm";
    }


    // 상품 상세 by User
    @GetMapping("/detail/{productId}")
    public String findProductUserDetailResponse(@PathVariable("productId") Long productId, Model model) {
        // 상품 상세 정보 조회
        ProductResponse product = productRepository.findAdminProductDetailResponse(productId);
        productService.increaseViewCount(productId); // 조회수 증가

        model.addAttribute("product", product);

        // 카테고리 정보 저장
        Long categoryNo = product.getCategoryResponse().getCategoryNo();
        model.addAttribute("middleCategoryName", categoryQueryService.findMiddleCategoryForSubCategory(categoryNo));
        model.addAttribute("childCategoryName", product.getCategoryResponse().getName());

        // 옵션 그룹별 존재하는 옵션값만 저장할 Map
        Map<String, List<OptionValueResponse>> itemMap = new HashMap<>();
        Map<Long, List<ItemResponse>> optionItemMap = new HashMap<>();

        // 상품에서 실제 사용된 옵션 그룹만 필터링
        Set<Long> usedOptionGroupNos = product.getItems().stream()
                .flatMap(item -> item.getOptionValues().stream())
                .map(OptionValueResponse::getOptionGroupNo)
                .collect(Collectors.toSet());

        List<OptionGroupResponse> filteredOptionGroups = product.getOptionGroups().stream()
                .filter(group -> usedOptionGroupNos.contains(group.getOptionGroupNo()))
                .collect(Collectors.toList());

        // 필터링된 옵션 그룹만 모델에 추가
        model.addAttribute("optionGroups", filteredOptionGroups);

        for (ItemResponse item : product.getItems()) {
            for (OptionValueResponse optionValue : item.getOptionValues()) {
                Long optionGroupNo = optionValue.getOptionGroupNo();
                String groupName = filteredOptionGroups.stream()
                        .filter(group -> group.getOptionGroupNo().equals(optionGroupNo))
                        .map(OptionGroupResponse::getGroupName)
                        .findFirst().orElse(null);

                if (groupName != null) {
                    // 옵션값을 그룹별로 저장 (중복 방지)
                    itemMap.computeIfAbsent(groupName, k -> new ArrayList<>());
                    if (itemMap.get(groupName).stream().noneMatch(o -> o.getOptionValueNo().equals(optionValue.getOptionValueNo()))) {
                        itemMap.get(groupName).add(optionValue);
                    }

                    optionItemMap.computeIfAbsent(optionValue.getOptionValueNo(), k -> new ArrayList<>()).add(item);
                }
            }
        }

        model.addAttribute("itemMap", itemMap);
        model.addAttribute("optionItemMap", optionItemMap);

        // 첫 렌더링 시 실시간 인기순 상품 50개를 넘겨준다.
        Page<ProductListResponse> viewRollingProducts = viewRollingRepository.getViewRollingProducts(categoryNo, REALTIME, PageRequest.of(0, 50));
        model.addAttribute("products", viewRollingProducts.getContent());  // 상품 리스트
        model.addAttribute("page", viewRollingProducts.getNumber());       // 현재 페이지 번호
        model.addAttribute("size", viewRollingProducts.getSize());         // 페이지 크기 (한 페이지 당 개수)
        model.addAttribute("totalElements", viewRollingProducts.getTotalElements());  // 전체 데이터 개수
        model.addAttribute("totalPages", viewRollingProducts.getTotalPages());        // 전체 페이지 개수
        model.addAttribute("hasNext", viewRollingProducts.hasNext());   // 다음 페이지 존재 여부
        model.addAttribute("hasPrevious", viewRollingProducts.hasPrevious()); // 이전 페이지 존재 여부
        model.addAttribute("categoryName", product.getCategoryResponse().getName());
        model.addAttribute("categoryNo", categoryNo);

        return "product/detail";
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
    @GetMapping({"/list", "/search"})
    public String getFilteredAndSortedProductList(
            HttpServletRequest request,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "24") int size,
            @RequestParam(name = "condition", required = false) Condition condition,
            @RequestParam(name = "order", defaultValue = "POPULAR", required = false) OrderBy order,
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

        // ✅ 검색 모드 여부 확인 (요청 URL이 "/search"라면 true)
        boolean isSearchMode = request.getRequestURI().contains("/search");

        // ✅ 검색 모드일 경우 검색어 저장
        if (isSearchMode && keyword != null && !keyword.isEmpty()) {
            searchLogService.saveSearchKeyword(keyword);
        }

        // ✅ 중분류 카테고리에 해당하는 하위 카테고리 조회
        if (condition != null && condition.isCategoryCondition()) {
            model.addAttribute("subCategoryTitle", condition.getCategoryName());
            model.addAttribute("subCategories", categoryQueryService.getSubCategoriesUnderMiddleCategoryWithCondition(condition));
        } else { // BEST, NEW, HANDMADE
            model.addAttribute("subCategoryTitle", condition != null ? condition.name() : "전체");
            if (condition != null && condition.name().equals("HANDMADE")) {
                model.addAttribute("subCategories", categoryQueryService.fetchHandmadeCategories());
            } else {
                model.addAttribute("subCategories", null);
            }
        }


        // ✅ 현재 선택된 카테고리 여부 설정 (전체 선택 여부 확인)
        boolean isCategorySelected = (category != null);

        // ✅ 페이지네이션 정보 추가
        model.addAttribute("isSearchMode", isSearchMode);
        model.addAttribute("products", productList.getContent());  // 상품 리스트
        model.addAttribute("page", productList.getNumber());       // 현재 페이지 번호
        model.addAttribute("size", productList.getSize());         // 페이지 크기 (한 페이지 당 개수)
        model.addAttribute("totalElements", productList.getTotalElements());  // 전체 데이터 개수
        model.addAttribute("totalPages", productList.getTotalPages());        // 전체 페이지 개수
        model.addAttribute("hasNext", productList.hasNext());   // 다음 페이지 존재 여부
        model.addAttribute("hasPrevious", productList.hasPrevious()); // 이전 페이지 존재 여부
        model.addAttribute("condition", condition);
        model.addAttribute("order", order);
        model.addAttribute("category", category);
        model.addAttribute("keyword", keyword);
        model.addAttribute("isCategorySelected", isCategorySelected);

        // 상품 리스트 페이지 반환
        return "product/list";
    }
}
