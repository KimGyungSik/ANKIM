package shoppingmall.ankim.domain.category.service.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CategoryCreateServiceRequest {
    private String name;
    private Long parentNo;
    private List<CategoryCreateServiceRequest> subCategories;

    @Builder
    private CategoryCreateServiceRequest(String name, Long parentNo, List<CategoryCreateServiceRequest> subCategories) {
        this.name = name;
        this.parentNo = parentNo;
        this.subCategories = subCategories;
    }
}
