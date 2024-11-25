package shoppingmall.ankim.domain.category.service.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CategoryUpdateServiceRequest {

    private final String name;       // 변경할 이름
    private final Long newParentNo;  // 변경할 부모 ID (소분류 전용)

    @Builder
    public CategoryUpdateServiceRequest(String name, Long newParentNo) {
        this.name = name;
        this.newParentNo = newParentNo;
    }
}
