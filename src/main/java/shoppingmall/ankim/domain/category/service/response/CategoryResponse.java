package shoppingmall.ankim.domain.category.service.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.category.entity.Category;

import java.util.ArrayList;
import java.util.List;
@NoArgsConstructor
@Getter
public class CategoryResponse {
    private Long categoryNo;
    private Long parentNo;
    private Long level;
    private String name;
    private List<CategoryResponse> childCategories;

    @Builder
    public CategoryResponse(Long categoryNo, Long parentNo, Long level, String name) {
        this.categoryNo = categoryNo;
        this.parentNo = parentNo;
        this.level = level;
        this.name = name;
        this.childCategories = new ArrayList<>(); // 빈 배열로 초기화
    }

    public static CategoryResponse of(Category category){
        return CategoryResponse.builder()
                .categoryNo(category.getNo())
                .parentNo(category.getParentNo())
                .level(category.getLevel())
                .name(category.getName())
                .build();
    }

    public void addChildCategories(CategoryResponse categoryDto){
        childCategories.add(categoryDto);
    }

    public void addChildCategories(List<CategoryResponse> categoryDto){
        childCategories.addAll(categoryDto);
    }
}