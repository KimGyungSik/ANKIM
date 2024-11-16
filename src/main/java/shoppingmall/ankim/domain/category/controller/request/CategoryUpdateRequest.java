package shoppingmall.ankim.domain.category.controller.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.category.service.request.CategoryCreateServiceRequest;
import shoppingmall.ankim.domain.category.service.request.CategoryUpdateServiceRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class CategoryUpdateRequest {

    @NotBlank(message = "카테고리 이름은 필수 입력 값입니다.")
    private String name;

    private Long newParentNo; // 새로운 부모 ID

    // 하위 카테고리 정보
    private List<CategoryUpdateRequest> childCategories;

    @Builder
    private CategoryUpdateRequest(String name, Long newParentNo, List<CategoryUpdateRequest> childCategories) {
        this.name = name;
        this.newParentNo = newParentNo;
        this.childCategories = childCategories;
    }

    public CategoryUpdateServiceRequest toServiceRequest() {
        List<CategoryUpdateServiceRequest> subCategoryRequests = (childCategories != null)
                ? childCategories.stream()
                .map(CategoryUpdateRequest::toServiceRequest)
                .collect(Collectors.toList())
                : List.of();

        return CategoryUpdateServiceRequest.builder()
                .name(this.name)
                .newParentNo(newParentNo)
                .childCategories(subCategoryRequests)
                .build();
    }
}
