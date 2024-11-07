package shoppingmall.ankim.domain.category.repository.query;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shoppingmall.ankim.domain.category.dto.CategoryResponse;
import shoppingmall.ankim.domain.category.entity.CategoryLevel;

import java.util.List;
import java.util.Optional;

import static shoppingmall.ankim.domain.category.entity.QCategory.*;

@Repository
@RequiredArgsConstructor
public class CategoryQueryRepositoryImpl implements CategoryQueryRepository{
    private final JPAQueryFactory queryFactory;

    // 모든 중분류와 그 하위 소분류 조회 (한 번의 쿼리로 계층적 데이터 가져오기)
    @Override
    public List<CategoryResponse> findAllMiddleCategoriesWithSubCategories() {
        return queryFactory
                .select(Projections.constructor(CategoryResponse.class,
                        category.no,
                        category.parent.no,
                        category.level,
                        category.name,
                        JPAExpressions
                                .select(Projections.constructor(CategoryResponse.class,
                                        category.no,
                                        category.parent.no,
                                        category.level,
                                        category.name))
                                .from(category)
                                .where(category.parent.no.eq(category.no))  // 하위 카테고리 조건
                ))
                .from(category)
                .where(category.level.eq(CategoryLevel.MIDDLE))
                .fetch();
    }

    // 특정 중분류에 속한 모든 소분류 조회
    @Override
    public List<CategoryResponse> findSubCategoriesByMiddleCategoryId(Long middleCategoryId) {
        return queryFactory
                .select(Projections.constructor(CategoryResponse.class,
                        category.no,
                        category.parent.no,
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
                .select(Projections.constructor(CategoryResponse.class,
                        category.parent.no,
                        category.parent.parent.no,
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
                .select(Projections.constructor(CategoryResponse.class,
                        category.no,
                        category.parent.no,
                        category.level,
                        category.name
                ))
                .from(category)
                .where(category.level.eq(CategoryLevel.MIDDLE))
                .fetch();
    }
}
