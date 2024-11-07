package shoppingmall.ankim.domain.category.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import shoppingmall.ankim.domain.category.dto.CategoryResponse;
import shoppingmall.ankim.domain.category.entity.Category;
import shoppingmall.ankim.global.config.QuerydslConfig;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static shoppingmall.ankim.domain.category.entity.CategoryLevel.*;


@DataJpaTest
@Import(QuerydslConfig.class) // QuerydslConfig를 테스트에 추가
class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @DisplayName("모든 중분류와 그 하위 소분류를 모두 조회할 수 있다")
    @Test
    void findAllMiddleCategoriesWithSubCategories() {
        // given
        Category sub1 = Category.builder()
                .name("코트")
                .build();
        Category sub2 = Category.builder()
                .name("자켓")
                .build();
        Category sub3 = Category.builder()
                .name("가디건")
                .build();
        Category sub4 = Category.builder()
                .name("티셔츠")
                .build();
        Category sub5 = Category.builder()
                .name("블라우스")
                .build();
        Category sub6 = Category.builder()
                .name("니트")
                .build();
        Category middle1 = Category.builder()
                .name("아우터")
                .subCategories(List.of(sub1,sub2,sub3))
                .build();
        Category middle2 = Category.builder()
                .name("상의")
                .subCategories(List.of(sub4,sub5,sub6))
                .build();

        categoryRepository.saveAll(List.of(middle1, middle2, sub1, sub2, sub3, sub4, sub5, sub6));

        // when
        List<CategoryResponse> allMiddleCategoriesWithSubCategories = categoryRepository.findAllMiddleCategoriesWithSubCategories();

        // then
        assertThat(allMiddleCategoriesWithSubCategories).hasSize(2)
                .extracting("parentNo", "level", "name", "childCategories")
                .containsExactlyInAnyOrder(
                        tuple(null, MIDDLE, "아우터", List.of(
                                new CategoryResponse(sub1.getNo(), middle1.getNo(), SUB, "코트", List.of()),
                                new CategoryResponse(sub2.getNo(), middle1.getNo(), SUB, "자켓", List.of()),
                                new CategoryResponse(sub3.getNo(), middle1.getNo(), SUB, "가디건", List.of())
                        )),
                        tuple(null, MIDDLE, "상의", List.of(
                                new CategoryResponse(sub4.getNo(), middle2.getNo(), SUB, "티셔츠", List.of()),
                                new CategoryResponse(sub5.getNo(), middle2.getNo(), SUB, "블라우스", List.of()),
                                new CategoryResponse(sub6.getNo(), middle2.getNo(), SUB, "니트", List.of())
                        ))
                );
    }

    @DisplayName("특정 중분류에 속한 모든 소분류를 조회할 수 있다")
    @Test
    void findSubCategoriesByMiddleCategoryId() {
        // given
        Category sub1 = Category.builder()
                .name("코트")
                .build();
        Category sub2 = Category.builder()
                .name("자켓")
                .build();
        Category sub3 = Category.builder()
                .name("가디건")
                .build();
        Category sub4 = Category.builder()
                .name("티셔츠")
                .build();
        Category sub5 = Category.builder()
                .name("블라우스")
                .build();
        Category sub6 = Category.builder()
                .name("니트")
                .build();
        Category middle1 = Category.builder()
                .name("아우터")
                .subCategories(List.of(sub1,sub2,sub3))
                .build();
        Category middle2 = Category.builder()
                .name("상의")
                .subCategories(List.of(sub4,sub5,sub6))
                .build();

        categoryRepository.saveAll(List.of(middle1, middle2, sub1, sub2, sub3, sub4, sub5, sub6));

        // when
        List<CategoryResponse> result = categoryRepository.findSubCategoriesByMiddleCategoryId(1L);

        // then
        assertThat(result).hasSize(3)
                .extracting("parentNo", "level", "name", "childCategories")
                .containsExactlyInAnyOrder(
                        tuple(1L, SUB, "코트", List.of()),
                        tuple(1L, SUB, "자켓", List.of()),
                        tuple(1L, SUB, "가디건", List.of())
                );
    }

    @DisplayName("소분류 ID로 해당 소분류의 상위 중분류가 무엇인지 조회할 수 있다")
    @Test
    void findMiddleCategoryBySubCategoryId() {
        // given
        Category sub1 = Category.builder()
                .name("코트")
                .build();
        Category sub2 = Category.builder()
                .name("자켓")
                .build();
        Category sub3 = Category.builder()
                .name("가디건")
                .build();
        Category sub4 = Category.builder()
                .name("티셔츠")
                .build();
        Category sub5 = Category.builder()
                .name("블라우스")
                .build();
        Category sub6 = Category.builder()
                .name("니트")
                .build();
        Category middle1 = Category.builder()
                .name("아우터")
                .subCategories(List.of(sub1,sub2,sub3))
                .build();
        Category middle2 = Category.builder()
                .name("상의")
                .subCategories(List.of(sub4,sub5,sub6))
                .build();

        categoryRepository.saveAll(List.of(middle1, middle2, sub1, sub2, sub3, sub4, sub5, sub6));

        // when
        Optional<CategoryResponse> middelCategory
                = categoryRepository.findMiddleCategoryBySubCategoryId(sub6.getNo());

        // then
        assertThat(middelCategory).isNotNull();
        assertThat(middelCategory.get().getName()).isEqualTo("상의");
        assertThat(middelCategory.get().getLevel()).isEqualTo(MIDDLE);
    }

    @DisplayName("중분류만 조회할 수 있다")
    @Test
    void findMiddleCategories() {
        // given
        Category sub1 = Category.builder()
                .name("코트")
                .build();
        Category sub2 = Category.builder()
                .name("자켓")
                .build();
        Category sub3 = Category.builder()
                .name("가디건")
                .build();
        Category sub4 = Category.builder()
                .name("티셔츠")
                .build();
        Category sub5 = Category.builder()
                .name("블라우스")
                .build();
        Category sub6 = Category.builder()
                .name("니트")
                .build();
        Category middle1 = Category.builder()
                .name("아우터")
                .subCategories(List.of(sub1,sub2,sub3))
                .build();
        Category middle2 = Category.builder()
                .name("상의")
                .subCategories(List.of(sub4,sub5,sub6))
                .build();

        categoryRepository.saveAll(List.of(middle1, middle2, sub1, sub2, sub3, sub4, sub5, sub6));

        // when
        List<CategoryResponse> middleCategories = categoryRepository.findMiddleCategories();

        // then
        assertThat(middleCategories).hasSize(2)
                .extracting("categoryNo", "level", "name")
                .containsExactlyInAnyOrder(
                        tuple(middle1.getNo(), MIDDLE, "아우터"),
                        tuple(middle2.getNo(), MIDDLE, "상의")
                );
    }


}