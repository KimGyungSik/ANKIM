package shoppingmall.ankim.domain.product.repository;

import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.product.dto.ProductListResponse;
import shoppingmall.ankim.domain.product.repository.query.ProductQueryRepository;
import shoppingmall.ankim.domain.product.repository.query.helper.*;
import shoppingmall.ankim.factory.ProductFactory;
import shoppingmall.ankim.global.config.QuerydslConfig;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static shoppingmall.ankim.factory.ProductFactory.*;

@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
@Import(QuerydslConfig.class) // QuerydslConfig를 테스트에 추가
public class ProductQueryRepositoryTest {
    @MockBean
    private S3Service s3Service;

    @Autowired
    EntityManager em;

    @Autowired
    ProductRepository productRepository;

    @BeforeEach
    void setup() {
        // 중분류 -> 소분류 구조 정의
        Map<String, List<String>> categoryStructure = new HashMap<>();
        categoryStructure.put("Top", List.of("티셔츠", "맨투맨"));
        categoryStructure.put("Outer", List.of("자켓", "코트"));
        categoryStructure.put("Knit", List.of("스웨터", "가디건"));

        // 테스트 데이터 생성
        ProductFactory.createTestProductsWithSubcategories(em, 10, 10, 10, 10, categoryStructure);
        createTestProductsWithColorOptions(em, 10);
    }

    @DisplayName("상품 정보 필터 조건별로 상품을 조회할 수 있다.")
    @ParameterizedTest(name = "{index}: 상품 정보 조건 = {0}")
    @EnumSource(InfoSearch.class) // InfoSearch Enum의 모든 값에 대해 테스트 실행
    void findUserProductListResponseWithProductInfoFilter(InfoSearch infoSearch) {
        // given
        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, null, null, null, null, null, null, null,null, List.of(infoSearch));

        // then
        assertThat(result).isNotEmpty();

        // 필터 조건 검증
        switch (infoSearch) {
            case FREESHIP:
                assertThat(result.getContent())
                        .allMatch(product -> "Y".equals(product.getFreeShip())); // 무료배송 여부 확인
                break;
            case EXCLUDE_OUT_OF_STOCK:
                assertThat(result.getContent())
                        .allMatch(product -> product.getQty() > 0); // 재고 확인
                break;
            case DISCOUNT_ONLY:
                assertThat(result.getContent())
                        .allMatch(product -> product.getDiscRate() > 0); // 할인율 확인
                break;
            case HANDMADE_ONLY:
                assertThat(result.getContent())
                        .allMatch(product -> "Y".equals(product.getHandMadeYn())); // 핸드메이드 여부 확인
                break;
            case NONE:
            default:
                assertThat(result.getContent()).isNotEmpty(); // 기본 조건 검증
                break;
        }
    }

    @DisplayName("여러 상품 정보 필터 조건을 조합하여 상품을 조회할 수 있다.")
    @ParameterizedTest(name = "{index}: 조건 조합 = {0}")
    @MethodSource("provideProductInfoFilters")
    void findUserProductListResponseWithMultipleProductInfoFilters(List<InfoSearch> infoSearches) {
        // given
        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(pageable, null, null, null, null, null, null, null,null, infoSearches);

        // then
        assertThat(result).isNotEmpty();

        // 조건별 검증
        for (InfoSearch infoSearch : infoSearches) {
            switch (infoSearch) {
                case FREESHIP:
                    assertThat(result.getContent())
                            .allMatch(product -> "Y".equals(product.getFreeShip()));
                    break;
                case EXCLUDE_OUT_OF_STOCK:
                    assertThat(result.getContent())
                            .allMatch(product -> product.getQty() > 0);
                    break;
                case DISCOUNT_ONLY:
                    assertThat(result.getContent())
                            .allMatch(product -> product.getDiscRate() > 0);
                    break;
                case HANDMADE_ONLY:
                    assertThat(result.getContent())
                            .allMatch(product -> "Y".equals(product.getHandMadeYn()));
                    break;
                default:
                    break;
            }
        }
    }

    static Stream<List<InfoSearch>> provideProductInfoFilters() {
        return Stream.of(
                List.of(InfoSearch.FREESHIP),
                List.of(InfoSearch.EXCLUDE_OUT_OF_STOCK),
                List.of(InfoSearch.DISCOUNT_ONLY),
                List.of(InfoSearch.HANDMADE_ONLY),
                List.of(InfoSearch.FREESHIP, InfoSearch.DISCOUNT_ONLY), // 조건 조합
                List.of(InfoSearch.EXCLUDE_OUT_OF_STOCK, InfoSearch.HANDMADE_ONLY) // 다른 조건 조합
        );
    }

    @DisplayName("가격 조건 필터링에 따라 상품을 조회할 수 있다.")
    @ParameterizedTest(name = "{index}: Price Condition = {0}")
    @EnumSource(PriceCondition.class) // PriceCondition Enum의 모든 값에 대해 테스트 실행
    void findUserProductListResponseWithPriceConditionFilter(PriceCondition priceCondition) {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        Integer customMinPrice = null; // 사용자 정의 최소 가격 (CUSTOM일 때만 사용)
        Integer customMaxPrice = null; // 사용자 정의 최대 가격 (CUSTOM일 때만 사용)

        if (priceCondition == PriceCondition.CUSTOM) {
            customMinPrice = 30000; // 사용자 정의 최소 가격
            customMaxPrice = 100000; // 사용자 정의 최대 가격
        }

        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(
                pageable,
                null, // condition
                null, // order
                null, // categoryId
                null, // keyword
                null,
                priceCondition, // PriceCondition
                customMinPrice, // 사용자 정의 최소 가격
                customMaxPrice, // 사용자 정의 최대 가격
                null // infoSearches
        );

        // then
        assertThat(result).isNotEmpty();

        // 검증: 상품의 가격이 조건에 만족하는지 확인
        for (ProductListResponse product : result.getContent()) {
            if (priceCondition == PriceCondition.CUSTOM) {
                // 사용자 정의 가격 조건 검증
                assertThat(product.getSellPrice()).isBetween(customMinPrice, customMaxPrice);
            } else {
                // 고정된 PriceCondition 검증
                if (priceCondition.getMinPrice() != null) {
                    assertThat(product.getSellPrice()).isGreaterThanOrEqualTo(priceCondition.getMinPrice());
                }
                if (priceCondition.getMaxPrice() != null) {
                    assertThat(product.getSellPrice()).isLessThanOrEqualTo(priceCondition.getMaxPrice());
                }
            }
        }
    }

    @DisplayName("사용자 정의 가격 조건을 조합하여 상품을 조회할 수 있다.")
    @Test
    void findUserProductListResponseWithCustomPriceFilter() {
        // given
        PageRequest pageable = PageRequest.of(0, 10);
        PriceCondition priceCondition = PriceCondition.CUSTOM; // 사용자 정의 조건
        Integer customMinPrice = 50000; // 사용자 정의 최소 가격
        Integer customMaxPrice = 200000; // 사용자 정의 최대 가격

        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(
                pageable,
                null, // condition
                null, // order
                null, // categoryId
                null, // keyword
                null,
                priceCondition, // PriceCondition
                customMinPrice, // 사용자 정의 최소 가격
                customMaxPrice, // 사용자 정의 최대 가격
                null // infoSearches
        );

        // then
        assertThat(result).isNotEmpty();

        // 검증: 상품의 가격이 사용자 정의 범위에 포함되는지 확인
        for (ProductListResponse product : result.getContent()) {
            assertThat(product.getSellPrice()).isBetween(customMinPrice, customMaxPrice);
        }
    }

    @DisplayName("상품 색상 조건 필터링 테스트")
    @ParameterizedTest(name = "{index}: 색상 조건 = {0}")
    @EnumSource(value = ColorCondition.class, names = {"BLACK", "WHITE", "RED", "BLUE", "GREEN"}) // 선택된 색상만 테스트
    void findUserProductListResponseWithColorFilter(ColorCondition colorCondition) {
        // given
        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(
                pageable, null, null, null, null, List.of(colorCondition), null,
                null, null, null);

        // then
        assertThat(result).isNotEmpty();

        // 필터링 검증: 색상 코드가 검색 키워드에 포함되는지 확인
        String colorHexCode = colorCondition.getHexCode();
        assertThat(result.getContent())
                .allMatch(product -> product.getSearchKeywords().contains(colorHexCode));
    }

    @DisplayName("색상 조건 조합 필터링 테스트")
    @ParameterizedTest(name = "{index}: 색상 조건 조합 = {0}")
    @MethodSource("provideColorConditions")
    void findUserProductListResponseWithColorCombinations(List<ColorCondition> colorConditions) {
        // given
        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(
                pageable, null, null, null, null, colorConditions, null,
                null, null, null);

        // then
        assertThat(result).isNotEmpty();

        // 필터링 검증: 반환된 상품들의 검색 키워드가 제공된 색상 중 하나의 코드와 일치하는지 확인
        List<String> colorHexCodes = colorConditions.stream()
                .map(ColorCondition::getHexCode)
                .toList();

        assertThat(result.getContent())
                .allMatch(product ->
                        colorHexCodes.stream().anyMatch(product.getSearchKeywords()::contains));
    }

    static Stream<List<ColorCondition>> provideColorConditions() {
        return Stream.of(
                List.of(ColorCondition.BLACK),                           // 단일 색상
                List.of(ColorCondition.RED, ColorCondition.WHITE),       // 두 가지 색상
                List.of(ColorCondition.BLUE, ColorCondition.GREEN, ColorCondition.RED), // 세 가지 색상
                List.of(ColorCondition.BLACK, ColorCondition.WHITE, ColorCondition.RED, ColorCondition.GREEN) // 네 가지 색상
        );
    }
}
