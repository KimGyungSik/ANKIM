package shoppingmall.ankim.domain.category.service.query;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.category.entity.Category;
import shoppingmall.ankim.domain.category.exception.CategoryNotFoundException;
import shoppingmall.ankim.domain.category.repository.CategoryRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@Transactional
@TestPropertySource(properties = "spring.sql.init.mode=never")
class CategoryQueryServiceTest {
    @Autowired
    CategoryQueryService categoryQueryService;

    @DisplayName("카테고리에 없는 중분류로 모든 소분류를 조회할 경우 예외가 발생한다")
    @Test
    void getSubCategoriesUnderMiddleCategory() {
        // given
        // when // then
        assertThatThrownBy(() -> categoryQueryService.getSubCategoriesUnderMiddleCategory(10L))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage("카테고리를 찾을 수 없습니다.");
    }

    @DisplayName("카테고리에 없는 소분류로 해당하는 중분류를 조회할 경우 예외가 발생한다")
    @Test
    void findMiddleCategoryForSubCategory() {
        // given
        // when// then
        assertThatThrownBy(() -> categoryQueryService.findMiddleCategoryForSubCategory(1L))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage("카테고리를 찾을 수 없습니다.");
    }

}