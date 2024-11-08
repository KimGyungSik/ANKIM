package shoppingmall.ankim.domain.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.category.dto.CategoryResponse;
import shoppingmall.ankim.domain.category.entity.Category;
import shoppingmall.ankim.domain.category.entity.CategoryLevel;
import shoppingmall.ankim.domain.category.exception.CategoryNotFoundException;
import shoppingmall.ankim.domain.category.exception.ChildCategoryExistsException;
import shoppingmall.ankim.domain.category.exception.DuplicateMiddleCategoryNameException;
import shoppingmall.ankim.domain.category.exception.DuplicateSubCategoryNameException;
import shoppingmall.ankim.domain.category.repository.CategoryRepository;
import shoppingmall.ankim.domain.category.service.request.CategoryCreateServiceRequest;
import shoppingmall.ankim.global.exception.ErrorCode;

import java.util.ArrayList;
import java.util.List;

import static shoppingmall.ankim.domain.category.entity.CategoryLevel.*;
import static shoppingmall.ankim.global.exception.ErrorCode.*;
import static shoppingmall.ankim.global.exception.ErrorCode.CATEGORY_NOT_FOUND;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    // 중분류와 소분류를 동시에 추가하거나, 중분류만 추가하거나, 소분류만 추가
    // 단, 중복된 이름의 카테고리, 소분류가 중분류의 이름으로 등록 금지
    public CategoryResponse createCategory(CategoryCreateServiceRequest request) {
        if (isMiddleCategoryRequest(request)) {
            // 중분류를 새로 추가하는 경우
            if (categoryRepository.existsByNameAndLevel(request.getName(), MIDDLE)) {
                throw new DuplicateMiddleCategoryNameException(DUPLICATE_MIDDLE_CATEGORY_NAME);
            }
            Category middleCategory = createMiddleCategory(request);
            return saveAndConvertToResponse(middleCategory);

        } else {
            // 기존 중분류에 소분류를 추가하는 경우
            Category parentCategory = findParentCategory(request.getParentNo());
            if (categoryRepository.existsByNameAndParentNo(request.getName(), request.getParentNo())) {
                throw new DuplicateSubCategoryNameException(DUPLICATE_SUB_CATEGORY_NAME);
            }
            addSubCategories(parentCategory, prepareSubCategoryRequests(request));
            return saveAndConvertToResponse(parentCategory);
        }
    }

    // 소분류, 중분류 삭제
    // 단, 중분류 삭제 시 소분류가 존재할 경우 삭제 못함
    public void deleteCategory(Long categoryId) {
        // 카테고리를 조회
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND));
        log.info(String.valueOf(category.getNo()));

        if (category.getLevel() == MIDDLE) {
            // 중분류인 경우: 소분류가 존재하는지 확인
            if (categoryRepository.existsByParentNo(categoryId)) {
                throw new ChildCategoryExistsException(CHILD_CATEGORY_EXISTS);
            }
        }else {
            Category parent = category.getParent();
            if (parent != null) {
                parent.getSubCategories().remove(category);  // 부모의 컬렉션에서 제거
            }
        }

        // 카테고리 삭제 진행 (소분류이거나 소분류가 없는 중분류)
        categoryRepository.deleteById(categoryId);
        log.info("delete 완료");
    }


    private boolean isMiddleCategoryRequest(CategoryCreateServiceRequest request) {
        return request.getParentNo() == null;
    }

    private Category createMiddleCategory(CategoryCreateServiceRequest request) {
        Category middleCategory = Category.create(request.getName());
        addSubCategories(middleCategory, request.getSubCategories());
        return middleCategory;
    }

    private Category findParentCategory(Long parentNo) {
        return categoryRepository.findById(parentNo)
                .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND));
    }

    private List<CategoryCreateServiceRequest> prepareSubCategoryRequests(CategoryCreateServiceRequest request) {
        return (request.getSubCategories() != null && !request.getSubCategories().isEmpty())
                ? request.getSubCategories()
                : List.of(request);
    }

    private CategoryResponse saveAndConvertToResponse(Category category) {
        Category savedCategory = categoryRepository.save(category);
        return CategoryResponse.of(savedCategory);
    }

    private void addSubCategories(Category parentCategory, List<CategoryCreateServiceRequest> subCategoryRequests) {
        if (subCategoryRequests != null && !subCategoryRequests.isEmpty()) {
            for (CategoryCreateServiceRequest subCategoryRequest : subCategoryRequests) {
                Category subCategory = Category.create(subCategoryRequest.getName());
                parentCategory.addSubCategory(subCategory);
            }
        }
    }
}

