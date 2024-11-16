package shoppingmall.ankim.domain.category.service.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
public class CategoryUpdateServiceRequest {

    private final String name;
    private final Long newParentNo; // 새로운 부모 ID
    private final List<CategoryUpdateServiceRequest> childCategories;

    @Builder
    public CategoryUpdateServiceRequest(String name, Long newParentNo, List<CategoryUpdateServiceRequest> childCategories) {
        this.name = name;
        this.newParentNo = newParentNo; // 부모 변경 처리
        this.childCategories = childCategories;
    }
}
