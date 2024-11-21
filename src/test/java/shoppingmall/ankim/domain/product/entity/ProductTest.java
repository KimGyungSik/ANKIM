package shoppingmall.ankim.domain.product.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shoppingmall.ankim.domain.option.entity.OptionGroup;
import shoppingmall.ankim.domain.option.entity.OptionValue;

import java.util.List;

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

    @DisplayName("옵션 그룹의 옵션 값들이 검색 키워드에 반영된다.")
    @Test
    void updateSearchKeywords() {
        // given
        Product product = Product.builder()
                .name("테스트 상품")
                .discRate(10)
                .origPrice(10000)
                .searchKeywords("기본 키워드")
                .build();

        // 옵션 그룹 및 옵션 값 설정
        OptionGroup colorGroup = OptionGroup.builder()
                .name("컬러")
                .optionValues(List.of(
                        OptionValue.builder().name("레드").build(),
                        OptionValue.builder().name("블루").build()
                ))
                .build();

        OptionGroup sizeGroup = OptionGroup.builder()
                .name("사이즈")
                .optionValues(List.of(
                        OptionValue.builder().name("M").build(),
                        OptionValue.builder().name("L").build()
                ))
                .build();

        product.getOptionGroups().add(colorGroup);
        product.getOptionGroups().add(sizeGroup);

        // when
        product.updateSearchKeywords();

        // then
        assertThat(product.getSearchKeywords())
                .contains("기본 키워드", "레드", "블루")
                .doesNotContain("M", "L");
    }

    @DisplayName("옵션 그룹이 없을 경우 검색 키워드는 기존 값과 동일하다.")
    @Test
    void updateSearchKeywordsWithoutOptionGroups() {
        // given
        Product product = Product.builder()
                .name("테스트 상품")
                .discRate(10)
                .origPrice(10000)
                .searchKeywords("기본 키워드")
                .build();

        // when
        product.updateSearchKeywords();

        // then
        assertThat(product.getSearchKeywords()).isEqualTo("기본 키워드");
    }

    @DisplayName("검색 키워드가 기존에 없을 경우 새롭게 추가된다.")
    @Test
    void updateSearchKeywordsWhenNoInitialKeywords() {
        // given
        Product product = Product.builder()
                .name("테스트 상품")
                .discRate(10)
                .origPrice(10000)
                .build();

        // 옵션 그룹 및 옵션 값 설정
        OptionGroup colorGroup = OptionGroup.builder()
                .name("컬러")
                .optionValues(List.of(
                        OptionValue.builder().name("그린").build(),
                        OptionValue.builder().name("옐로우").build()
                ))
                .build();

        product.getOptionGroups().add(colorGroup);

        // when
        product.updateSearchKeywords();

        // then
        assertThat(product.getSearchKeywords()).contains("그린", "옐로우");
    }

}