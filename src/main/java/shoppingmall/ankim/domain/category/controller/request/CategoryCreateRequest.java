package shoppingmall.ankim.domain.category.controller.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.category.service.request.CategoryCreateServiceRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class CategoryCreateRequest {

    @NotBlank(message = "카테고리 이름은 필수 입력 값입니다.")
    private String name;

    // 중분류일 때는 null일 수 있지만, 소분류일 때는 상위 카테고리 ID가 필요합니다.
    private Long parentNo;

    // 중분류와 소분류를 동시에 추가할 경우 하위 카테고리 목록
    private List<CategoryCreateRequest> childCategories = new ArrayList<>();

    @Builder
    private CategoryCreateRequest(String name, Long parentNo, List<CategoryCreateRequest> childCategories) {
        this.name = name;
        this.parentNo = parentNo;
        this.childCategories = childCategories;
    }

    public CategoryCreateServiceRequest toServiceRequest() {
        List<CategoryCreateServiceRequest> subCategoryRequests = (childCategories != null)
                ? childCategories.stream()
                .map(CategoryCreateRequest::toServiceRequest)
                .collect(Collectors.toList())
                : new ArrayList<>();

        return CategoryCreateServiceRequest.builder()
                .name(this.name)
                .parentNo(this.parentNo)
                .childCategories(subCategoryRequests)
                .build();
    }
}
