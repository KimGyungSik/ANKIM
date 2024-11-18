package shoppingmall.ankim.domain.product.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @DisplayName("원가의 할인율을 적용하여 판매가를 계산할 수 있다.")
    @Test
    void calculateSellPrice() {
        // given
        int origPrice = 10000; // 원가
        int discRate = 20; // 할인율 (%)

        // when
        Product product = Product.builder()
                .name("테스트 상품")
                .origPrice(origPrice)
                .discRate(discRate)
                .build();

        // then
        int expectedSellPrice = origPrice - (origPrice * discRate / 100);
        assertThat(product.getSellPrice()).isEqualTo(expectedSellPrice);
    }

    @DisplayName("할인율이 0%일 경우 판매가는 원가와 동일하다.")
    @Test
    void calculateSellPriceWithZeroDiscount() {
        // given
        int origPrice = 15000; // 원가
        int discRate = 0; // 할인율 (%)

        // when
        Product product = Product.builder()
                .name("테스트 상품")
                .origPrice(origPrice)
                .discRate(discRate)
                .build();

        // then
        assertThat(product.getSellPrice()).isEqualTo(origPrice);
    }

    @DisplayName("할인율이 100%일 경우 판매가는 0원이 된다.")
    @Test
    void calculateSellPriceWithFullDiscount() {
        // given
        int origPrice = 20000; // 원가
        int discRate = 100; // 할인율 (%)

        // when
        Product product = Product.builder()
                .name("테스트 상품")
                .origPrice(origPrice)
                .discRate(discRate)
                .build();

        // then
        assertThat(product.getSellPrice()).isEqualTo(0);
    }
}