package shoppingmall.ankim.domain.category.controller.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.category.entity.CategoryLevel;

@Getter
@NoArgsConstructor
public class CategoryCreateRequest {

    @NotBlank(message = "카테고리 이름은 필수 입력 값입니다.")
    private String name;

    @NotNull(message = "카테고리 레벨은 필수 입력 값입니다.")
    private CategoryLevel level; // 중분류(MIDDLE) 또는 소분류(SUB) 여부를 나타냅니다.

    // 중분류일 때는 null일 수 있지만, 소분류일 때는 상위 카테고리 ID가 필요합니다.
    private Long parentNo;

    public CategoryCreateRequest(String name, CategoryLevel level, Long parentNo) {
        this.name = name;
        this.level = level;
        this.parentNo = parentNo;
    }
}