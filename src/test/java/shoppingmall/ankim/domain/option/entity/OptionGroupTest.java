package shoppingmall.ankim.domain.option.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shoppingmall.ankim.domain.option.exception.DuplicateOptionValueException;
import shoppingmall.ankim.domain.product.entity.Product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class OptionGroupTest {

    @DisplayName("새로운 옵션값을 추가하려고 할 때 중독되는 이름은 추가할 수 없다.")
    @Test
    void addOptionValueDuplicatedName() {
        // given
        Product product = createProduct(); // Product 생성 (필요 시 Mock 처리)
        OptionGroup optionGroup = OptionGroup.create("색상", product);

        OptionValue optionValue1 = OptionValue.create(optionGroup, "Red", "#FF0000");
        OptionValue optionValue2 = OptionValue.create(optionGroup, "Red", "#FF0000"); // 중복 이름

        optionGroup.addOptionValue(optionValue1);

        // when & then
        assertThrows(DuplicateOptionValueException.class, () -> optionGroup.addOptionValue(optionValue2));
        assertThat(optionGroup.getOptionValues()).hasSize(1); // 여전히 하나의 옵션 값만 존재
    }

    private Product createProduct() {
        return Product.builder()
                .name("Test Product")
                .build();
    }

}