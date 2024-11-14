package shoppingmall.ankim.domain.category.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.category.entity.Category;
import shoppingmall.ankim.domain.category.entity.CategoryLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class CategoryResponse {
    private Long categoryNo;
    private Long parentNo;
    private CategoryLevel level;
    private String name;
    private List<CategoryResponse> childCategories = new ArrayList<>(); // 빈 리스트로 초기화

    @Builder
    public CategoryResponse(Long categoryNo, Long parentNo, CategoryLevel level, String name, List<CategoryResponse> childCategories) {
        this.categoryNo = categoryNo;
        this.parentNo = parentNo;
        this.level = level;
        this.name = name;
        this.childCategories = childCategories != null ? childCategories : new ArrayList<>(); // null 방지
    }

    public static CategoryResponse of(Category category) {
        return CategoryResponse.builder()
                .categoryNo(category.getNo())
                .parentNo(category.getParent() != null ? category.getParent().getNo() : null)
                .level(category.getLevel())
                .name(category.getName())
                .childCategories(category.getChildCategories().stream()
                        .map(CategoryResponse::of)
                        .collect(Collectors.toList())
                )
                .build();
    }
}
