package shoppingmall.ankim.domain.category.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shoppingmall.ankim.domain.category.exception.CategoryNameTooLongException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class CategoryTest {
    @DisplayName("카테고리 이름을 50자 이상으로 입력할 경우 예외가 발생한다")
    @Test
    void validateName() {
        // given
        String longName = "A".repeat(51); // 51자로 구성된 문자열 생성

        // when // then
        assertThatThrownBy(() -> Category.builder()
                .name(longName)
                .build())
                .isInstanceOf(CategoryNameTooLongException.class)
                .hasMessageContaining("카테고리 이름은 50자 이하로 입력해야 합니다.");
    }
}