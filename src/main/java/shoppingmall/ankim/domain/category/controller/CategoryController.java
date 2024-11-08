package shoppingmall.ankim.domain.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // 모든 중분류와 그 하위 소분류 조회
    @GetMapping("/total")
    public ApiResponse<List<CategoryResponse>> getTotalCategories() {
        return ApiResponse.ok(categoryQueryService.fetchAllMiddleCategoriesWithSubCategories());
    }

    @PostMapping
    public ResponseEntity<Void> createCategory(@RequestBody @Valid CategoryCreateRequest request) {
        CategoryCreateServiceRequest serviceRequest = request.toServiceRequest();
        CategoryResponse response = categoryService.createCategory(serviceRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 특정 중분류에 속한 모든 소분류 조회
    // 소분류 ID로 해당 소분류의 상위 중분류 조회
    // 중분류만 조회
    // 중분류, 소분류 삭제


}
