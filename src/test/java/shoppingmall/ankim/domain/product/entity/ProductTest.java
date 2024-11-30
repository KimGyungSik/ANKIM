package shoppingmall.ankim.domain.product.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shoppingmall.ankim.domain.image.entity.ProductImg;
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

    @DisplayName("옵션 그룹의 색상 코드들이 검색 키워드에 반영된다.")
    @Test
    void updateSearchKeywordsWithColorCodes() {
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
                        OptionValue.builder().name("레드").colorCode("#FF0000").build(),
                        OptionValue.builder().name("블루").colorCode("#0000FF").build()
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
                .contains("기본 키워드", "#FF0000", "#0000FF") // 색상 코드 검증
                .doesNotContain("M", "L"); // 사이즈는 검색 키워드에 반영되지 않음
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

    @DisplayName("검색 키워드가 기존에 없을 경우 색상 코드가 새롭게 추가된다.")
    @Test
    void updateSearchKeywordsWithColorCodesWhenNoInitialKeywords() {
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
                        OptionValue.builder().name("그린").colorCode("#00FF00").build(),
                        OptionValue.builder().name("옐로우").colorCode("#FFFF00").build()
                ))
                .build();

        product.getOptionGroups().add(colorGroup);

        // when
        product.updateSearchKeywords();

        // then
        assertThat(product.getSearchKeywords())
                .contains("#00FF00", "#FFFF00"); // 색상 코드 검증
    }

    @DisplayName("썸네일이면서 순서가 1인 이미지를 가져올 수 있다.")
    @Test
    void getThumbnailImgUrl_withRepImgAndOrd1() {
        // given
        Product product = Product.builder()
                .name("테스트 상품")
                .origPrice(20000)
                .discRate(10)
                .build();

        // 썸네일 이미지 추가 (조건: 썸네일이면서 순서가 1)
        ProductImg thumbnailImg = ProductImg.create(
                "img1.jpg",
                "original1.jpg",
                "/images/img1.jpg",
                "Y", // 썸네일 여부
                1,  // 순서
                product
        );

        // 순서가 2인 썸네일 이미지 추가 (조건에 맞지 않음)
        ProductImg nonMatchingImg = ProductImg.create(
                "img2.jpg",
                "original2.jpg",
                "/images/img2.jpg",
                "Y",
                2,
                product
        );

        product.addProductImg(thumbnailImg);
        product.addProductImg(nonMatchingImg);

        // when
        String thumbnailUrl = product.getThumbnailImgUrl();

        // then
        assertThat(thumbnailUrl).isEqualTo("/images/img1.jpg"); // 썸네일이면서 ord=1인 이미지 URL
    }

    @DisplayName("썸네일이 없거나 조건에 맞는 이미지가 없을 경우 null을 반환한다.")
    @Test
    void getThumbnailImgUrl_whenNoMatchingImage() {
        // given
        Product product = Product.builder()
                .name("테스트 상품")
                .origPrice(20000)
                .discRate(10)
                .build();

        // 썸네일이 아니거나 순서가 1이 아닌 이미지 추가
        ProductImg nonThumbnailImg = ProductImg.create(
                "img1.jpg",
                "original1.jpg",
                "/images/img1.jpg",
                "N", // 썸네일 아님
                1,
                product
        );

        ProductImg nonMatchingOrdImg = ProductImg.create(
                "img2.jpg",
                "original2.jpg",
                "/images/img2.jpg",
                "Y", // 썸네일
                2,  // 순서가 1이 아님
                product
        );

        product.addProductImg(nonThumbnailImg);
        product.addProductImg(nonMatchingOrdImg);

        // when
        String thumbnailUrl = product.getThumbnailImgUrl();

        // then
        assertThat(thumbnailUrl).isNull(); // 조건에 맞는 이미지가 없으므로 null 반환
    }


}