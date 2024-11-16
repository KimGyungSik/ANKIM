package shoppingmall.ankim.domain.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.category.dto.CategoryResponse;
import shoppingmall.ankim.domain.category.entity.Category;
import shoppingmall.ankim.domain.category.exception.*;
import shoppingmall.ankim.domain.category.repository.CategoryRepository;
import shoppingmall.ankim.domain.category.service.request.CategoryCreateServiceRequest;
import shoppingmall.ankim.domain.category.service.request.CategoryUpdateServiceRequest;
import shoppingmall.ankim.domain.product.repository.ProductRepository;

import java.util.List;

import static shoppingmall.ankim.domain.category.entity.CategoryLevel.MIDDLE;
import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

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
    // 조건 1. 삭제하고 싶은 카테고리가 어떤 상품에 속해져 있으면 삭제할 수 없음
    // 조건 2. 중분류 삭제 시 소분류가 존재할 경우 삭제 못함
    public void deleteCategory(Long categoryId) {
        // 카테고리를 조회
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND));
        log.info(String.valueOf(category.getNo()));

        // 삭제하고 싶은 카테고리에 상품이 속해져 있는지 확인
        // 카테고리에 속한 상품이 있을 경우 예외
        if(productRepository.existsByCategory(category)) {
            throw new CategoryLinkedWithProductException(CATEGORY_LINKED_WITH_PRODUCT);
        }


        if (category.getLevel() == MIDDLE) {
            // 중분류인 경우: 소분류가 존재하는지 확인
            if (categoryRepository.existsByParentNo(categoryId)) {
                throw new ChildCategoryExistsException(CHILD_CATEGORY_EXISTS);
            }
        }else {
            Category parent = category.getParent();
            if (parent != null) {
                parent.getChildCategories().remove(category);  // 부모의 컬렉션에서 제거
            }
        }

        // 카테고리 삭제 진행 (소분류이거나 소분류가 없는 중분류)
        categoryRepository.deleteById(categoryId);
        log.info("delete 완료");
    }

    // 카테고리 수정
    // 해당 카테고리 이름 변경 가능, 중분류에 속하는 소분류 이름도 변경 가능 -> 동시 수정 가능
    // 소분류의 부모인 중분류를 수정할 수 있음
    // 조건 1. 해당 카테고리에 속해져 있는 상품의 카테고리는 자동 변경
    // 카테고리 수정 -> 해당 카테고리에 속해 있는 상품의 카테고리를 자동 변경
    public void updateCategory(Long categoryId, CategoryUpdateServiceRequest request) {
        // 1. 기존 카테고리 조회
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND));

        // 2. 카테고리 이름 업데이트
        category.updateName(request.getName());

        // 3. 새로운 부모 설정 (중분류의 경우만 처리)
        if (request.getNewParentNo() != null) {
            Category newParentCategory = categoryRepository.findById(request.getNewParentNo())
                    .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND));
            category.changeParentCategory(newParentCategory); // 부모 변경

            // 중분류 변경 시 해당 카테고리에 속한 상품 업데이트
            productRepository.updateCategoryForProducts(categoryId, newParentCategory);
        }

        // 4. 하위 카테고리 수정 처리
        if (request.getChildCategories() != null && !request.getChildCategories().isEmpty()) {
            updateSubCategories(category, request.getChildCategories());
        }
    }


    // 하위 카테고리 재귀적으로 수정
    private void updateSubCategories(Category parentCategory, List<CategoryUpdateServiceRequest> subCategoryRequests) {
        for (CategoryUpdateServiceRequest childRequest : subCategoryRequests) {
            Category existingChild = parentCategory.getChildCategories()
                    .stream()
                    .filter(child -> child.getName().equals(childRequest.getName()))
                    .findFirst()
                    .orElse(null);

            if (existingChild != null) {
                // 이미 존재하는 하위 카테고리 이름 수정
                existingChild.updateName(childRequest.getName());

                // 해당 소분류에 속한 상품들의 카테고리 업데이트
                productRepository.updateCategoryForProducts(existingChild.getNo(), existingChild);
            } else {
                // 새로운 하위 카테고리 추가
                Category newChild = Category.create(childRequest.getName());
                parentCategory.addSubCategory(newChild);
            }
        }
    }



    private boolean isMiddleCategoryRequest(CategoryCreateServiceRequest request) {
        return request.getParentNo() == null;
    }

    private Category createMiddleCategory(CategoryCreateServiceRequest request) {
        Category middleCategory = Category.create(request.getName());
        addSubCategories(middleCategory, request.getChildCategories());
        return middleCategory;
    }

    private Category findParentCategory(Long parentNo) {
        return categoryRepository.findById(parentNo)
                .orElseThrow(() -> new CategoryNotFoundException(CATEGORY_NOT_FOUND));
    }

    private List<CategoryCreateServiceRequest> prepareSubCategoryRequests(CategoryCreateServiceRequest request) {
        return (request.getChildCategories() != null && !request.getChildCategories().isEmpty())
                ? request.getChildCategories()
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

