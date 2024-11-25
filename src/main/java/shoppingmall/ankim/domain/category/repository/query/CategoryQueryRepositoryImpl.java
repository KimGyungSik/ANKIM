package shoppingmall.ankim.domain.category.repository.query;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shoppingmall.ankim.domain.category.dto.CategoryResponse;
import shoppingmall.ankim.domain.category.entity.Category;
import shoppingmall.ankim.domain.category.entity.CategoryLevel;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static shoppingmall.ankim.domain.category.entity.QCategory.category;

@Repository
@RequiredArgsConstructor
public class CategoryQueryRepositoryImpl implements CategoryQueryRepository{
    private final JPAQueryFactory queryFactory;

    // 모든 중분류와 그 하위 소분류 조회 (한 번의 쿼리로 계층적 데이터 가져오기)
    @Override
    public List<CategoryResponse> findAllMiddleCategoriesWithSubCategories() {
        List<Category> categories = queryFactory
                .selectFrom(category)
                .leftJoin(category.childCategories).fetchJoin()
                .where(category.level.eq(CategoryLevel.MIDDLE))
                .fetch();

        // 중분류 및 하위 소분류 카테고리를 DTO로 변환하면서 계층 구조로 매핑
        return categories.stream()
                .map(this::toCategoryResponseWithSubCategories)
                .collect(Collectors.toList());
    }


    // 특정 중분류에 속한 모든 소분류 조회
    @Override
    public List<CategoryResponse> findSubCategoriesByMiddleCategoryId(Long middleCategoryId) {
        return queryFactory
                .select(Projections.bean(CategoryResponse.class,
                        category.no.as("categoryNo"),
                        category.parent.no.as("parentNo"),
                        category.level,
                        category.name
                ))
                .from(category)
                .where(category.parent.no.eq(middleCategoryId)
                        .and(category.level.eq(CategoryLevel.SUB)))
                .fetch();
    }


    // 소분류 ID로 해당 소분류의 상위 중분류 조회
    @Override
    public Optional<CategoryResponse> findMiddleCategoryBySubCategoryId(Long subCategoryId) {
        CategoryResponse middleCategory = queryFactory
                .select(Projections.bean(CategoryResponse.class,
                        category.parent.no.as("categoryNo"),
                        category.parent.parent.no.as("parentNo"),
                        category.parent.level,
                        category.parent.name
                ))
                .from(category)
                .where(category.no.eq(subCategoryId)
                        .and(category.level.eq(CategoryLevel.SUB)))
                .fetchOne();

        return Optional.ofNullable(middleCategory);
    }

    // 중분류만 조회
    @Override
    public List<CategoryResponse> findMiddleCategories() {
        return queryFactory
                .select(Projections.bean(CategoryResponse.class,
                        category.no.as("categoryNo"),
                        category.parent.no.as("parentNo"),
                        category.level,
                        category.name
                ))
                .from(category)
                .where(category.level.eq(CategoryLevel.MIDDLE))
                .fetch();
    }

    // Category 엔티티를 계층 구조를 유지하며 CategoryResponse로 변환하는 메서드
    private CategoryResponse toCategoryResponseWithSubCategories(Category category) {
        List<CategoryResponse> childResponses = category.getChildCategories().stream()
                .map(this::toCategoryResponseWithSubCategories) // 재귀적으로 하위 소분류 매핑
                .collect(Collectors.toList());

        return CategoryResponse.builder()
                .categoryNo(category.getNo())
                .parentNo(category.getParent() != null ? category.getParent().getNo() : null)
                .level(category.getLevel())
                .name(category.getName())
                .childCategories(childResponses)
                .build();
    }
}
