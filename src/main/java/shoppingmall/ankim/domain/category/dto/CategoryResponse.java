package shoppingmall.ankim.domain.category.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.category.entity.CategoryLevel;

import java.util.ArrayList;
import java.util.List;

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
}
