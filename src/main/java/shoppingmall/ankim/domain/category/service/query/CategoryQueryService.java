package shoppingmall.ankim.domain.category.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.category.dto.CategoryResponse;
import shoppingmall.ankim.domain.category.exception.CategoryNotFoundException;
import shoppingmall.ankim.domain.category.repository.CategoryRepository;
import shoppingmall.ankim.domain.product.repository.query.helper.Condition;

import java.util.List;
import java.util.stream.Collectors;

import static shoppingmall.ankim.global.exception.ErrorCode.CATEGORY_IS_EMPTY;
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
            throw new CategoryNotFoundException(CATEGORY_IS_EMPTY);
        }

        return subCategories;
    }

    // 특정 중분류에 속한 모든 소분류 조회
    public List<CategoryResponse> getSubCategoriesUnderMiddleCategoryWithCondition(Condition condition) {
        List<CategoryResponse> subCategories = categoryRepository.findSubCategoriesByMiddleCategoryName(condition);

        // 빈 리스트일 경우 예외 발생
        if (subCategories.isEmpty()) {
            throw new CategoryNotFoundException(CATEGORY_IS_EMPTY);
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

    // 소분류만 조회
    public List<CategoryResponse> fetchAllSubCategories() {
        // 소분류만 가져오도록 필터링
        List<CategoryResponse> responses = categoryRepository.findAll()
                .stream()
                .filter(category -> category.getParent() != null) // 소분류만 가져오기
                .map(CategoryResponse::of)
                .collect(Collectors.toList());
        if(responses==null || responses.isEmpty()) {
            throw new CategoryNotFoundException(CATEGORY_NOT_FOUND);
        }
        return responses;
    }

    // 핸드메이드 소분류 카테고리 조회
    public List<CategoryResponse> fetchHandmadeCategories() {
        // HANDMADE와 관련된 중분류 카테고리만 가져오기
        List<CategoryResponse> responses = categoryRepository.findAllByNames(List.of("OUTER", "TOP", "BOTTOM", "OPS/SK"))
                .stream()
                .map(CategoryResponse::of)
                .collect(Collectors.toList());
        if(responses==null || responses.isEmpty()) {
            throw new CategoryNotFoundException(CATEGORY_NOT_FOUND);
        }
        return responses;
    }
}
