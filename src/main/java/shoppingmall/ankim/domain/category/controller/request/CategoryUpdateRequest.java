package shoppingmall.ankim.domain.category.controller.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.category.service.request.CategoryUpdateServiceRequest;

@Getter
@NoArgsConstructor
public class CategoryUpdateRequest {

    private String name;       // 변경할 이름
    private Long newParentNo;  // 변경할 부모 ID (소분류 전용)

    @Builder
    private CategoryUpdateRequest(String name, Long newParentNo) {
        this.name = name;
        this.newParentNo = newParentNo;
    }

    public CategoryUpdateServiceRequest toServiceRequest() {
        return CategoryUpdateServiceRequest.builder()
                .name(this.name)
                .newParentNo(this.newParentNo)
                .build();
    }
}
