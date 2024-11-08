package shoppingmall.ankim.domain.category.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import shoppingmall.ankim.domain.category.controller.request.CategoryCreateRequest;
import shoppingmall.ankim.domain.category.dto.CategoryResponse;
import shoppingmall.ankim.domain.category.service.CategoryService;
import shoppingmall.ankim.domain.category.service.query.CategoryQueryService;
import shoppingmall.ankim.domain.category.service.request.CategoryCreateServiceRequest;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryQueryService categoryQueryService;

    // 중분류, 소분류 등록
    @PostMapping("/new")
    public ApiResponse<CategoryResponse> createCategory(@Valid @RequestBody CategoryCreateRequest request) {
        return ApiResponse.ok(categoryService.createCategory(request.toServiceRequest()));
    }

    // 중분류, 소분류 삭제
    @DeleteMapping("/{categoryId}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ApiResponse.ok();
    }

    // 모든 중분류와 그 하위 소분류 조회
    @GetMapping("/total")
    public ApiResponse<List<CategoryResponse>> getTotalCategories() {
        return ApiResponse.ok(categoryQueryService.fetchAllMiddleCategoriesWithSubCategories());
    }

    // 특정 중분류에 속한 모든 소분류 조회
    @GetMapping("/subcategories")
    public ApiResponse<List<CategoryResponse>> searchSubCategoriesUnderMiddleCategory(
            @RequestParam(value = "middleCategoryId") Long middleCategoryId) {
        return ApiResponse.ok(categoryQueryService.getSubCategoriesUnderMiddleCategory(middleCategoryId));
    }

    // 소분류 ID로 해당 소분류의 상위 중분류 조회
    @GetMapping("/parent")
    public ApiResponse<CategoryResponse> findMiddleCategoryForSubCategory(
            @RequestParam(value = "subCategoryId") Long subCategoryId) {
        return ApiResponse.ok(categoryQueryService.findMiddleCategoryForSubCategory(subCategoryId));
    }

    // 중분류만 조회
    @GetMapping("/middle")
    public ApiResponse<List<CategoryResponse>> getMiddleCategories() {
        return ApiResponse.ok(categoryQueryService.retrieveMiddleCategories());
    }
}

