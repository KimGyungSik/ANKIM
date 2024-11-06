package shoppingmall.ankim.domain.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shoppingmall.ankim.domain.category.service.CategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    //조회
//    @GetMapping("/total")
//    public ApiResponse<List<CategoryResponse>> getTotalCategories() {
//        return ApiResponse.ok(categoryService.getTotalCategories());
//    }
}
