package shoppingmall.ankim.domain.category.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.category.dto.CategoryResponse;
import shoppingmall.ankim.domain.category.exception.CategoryNotFoundException;
import shoppingmall.ankim.domain.category.repository.CategoryRepository;

import java.util.List;

import static shoppingmall.ankim.global.exception.ErrorCode.CATEGORY_NOT_FOUND;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class CategoryQueryService {
    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> fetchAllMiddleCategoriesWithSubCategories() {
        return categoryRepository.findAllMiddleCategoriesWithSubCategories();
    }

    // 특정 중분류에 속한 모든 소분류 조회
    public List<CategoryResponse> getSubCategoriesUnderMiddleCategory(Long middleCategoryId) {
        List<CategoryResponse> subCategories = categoryRepository.findSubCategoriesByMiddleCategoryId(middleCategoryId);

        // 빈 리스트일 경우 예외 발생
        if (subCategories.isEmpty()) {
            throw new CategoryNotFoundException(CATEGORY_NOT_FOUND);
        }

        return subCategories;
    }

    // 소분류 ID로 해당 소분류의 상위 중분류 조회
    public CategoryResponse findMiddleCategoryForSubCategory(Long subCategoryId) {
        // empty를 반환하면 예외 발생
        return categoryRepository.findMiddleCategoryBySubCategoryId(subCategoryId)
                .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND));
    }

    public List<CategoryResponse> retrieveMiddleCategories() {
        return categoryRepository.findMiddleCategories();
    }
}
