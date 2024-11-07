package shoppingmall.ankim.domain.category.repository.query;

import shoppingmall.ankim.domain.category.dto.CategoryResponse;

import java.util.List;
import java.util.Optional;

public interface CategoryQueryRepository {
    // 모든 중분류와 그 하위 소분류 조회
    List<CategoryResponse> findAllMiddleCategoriesWithSubCategories();

    // 특정 중분류에 속한 모든 소분류 조회
    List<CategoryResponse> findSubCategoriesByMiddleCategoryId(Long middleCategoryId);

    // 소분류 ID로 해당 소분류의 상위 중분류 조회
    Optional<CategoryResponse> findMiddleCategoryBySubCategoryId(Long subCategoryId);

    // 중분류만 조회
    List<CategoryResponse> findMiddleCategories();
}
