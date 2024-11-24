package shoppingmall.ankim.domain.product.repository;

import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
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
import shoppingmall.ankim.domain.product.repository.query.helper.*;
import shoppingmall.ankim.global.config.QuerydslConfig;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static shoppingmall.ankim.factory.ProductFactory.createTestProductsFromConditions;
@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
@Import(QuerydslConfig.class) // QuerydslConfig를 테스트에 추가
public class AllConditionsTest {
    @MockBean
    private S3Service s3Service;

    @Autowired
    EntityManager em;

    @Autowired
    ProductRepository productRepository;


    @BeforeEach
    void setup() {
        createTestProductsFromConditions(em, provideAllConditions());
        em.flush(); // 데이터베이스에 반영
        em.clear(); // 영속성 컨텍스트 초기화
    }


    @DisplayName("모든 조건(키워드, 카테고리, 정렬, 색상, 가격, 정보 필터링)을 조합하여 상품을 조회할 수 있다.")
    @ParameterizedTest(name = "{index}: 조건 조합 = {0}")
    @MethodSource("provideAllConditions")
    void findUserProductListResponseWithAllConditions(
            TestCondition testCondition // 테스트 조건 객체
    ) {
        // given
        PageRequest pageable = PageRequest.of(0, 10);

        // 카테고리 ID 가져오기 (필요한 경우)
        Long categoryId = null;
        if (testCondition.getCategoryName() != null) {
            categoryId = em.createQuery(
                            "SELECT c.no FROM Category c WHERE c.name = :name", Long.class)
                    .setParameter("name", testCondition.getCategoryName())
                    .getSingleResult();
        }

        // when
        Page<ProductListResponse> result = productRepository.findUserProductListResponse(
                pageable,
                testCondition.getCondition(),
                testCondition.getOrder(),
                categoryId,
                testCondition.getKeyword(),
                testCondition.getColorConditions(),
                testCondition.getPriceCondition(),
                testCondition.getCustomMinPrice(),
                testCondition.getCustomMaxPrice(),
                testCondition.getInfoSearches()
        );

        // then
        assertThat(result).isNotEmpty();

        // 검증 1: 검색 키워드 만족
        if (testCondition.getKeyword() != null) {
            assertThat(result.getContent())
                    .allMatch(product ->
                            product.getName().toLowerCase().contains(testCondition.getKeyword().toLowerCase()) ||
                                    product.getDesc().toLowerCase().contains(testCondition.getKeyword().toLowerCase()) ||
                                    product.getSearchKeywords().toLowerCase().contains(testCondition.getKeyword().toLowerCase()));
        }

        // 검증 2: 카테고리 필터링 만족
        if (testCondition.getCategoryName() != null) {
            assertThat(result.getContent())
                    .allMatch(product -> product.getCategoryName().equals(testCondition.getCategoryName()));
        }

        // 검증 3: 정렬 조건 만족
        if (ORDER_VALIDATORS.containsKey(testCondition.getOrder())) {
            List<ProductListResponse> products = result.getContent();
            assertThat(products).isSortedAccordingTo(ORDER_VALIDATORS.get(testCondition.getOrder()));
        }

        // 검증 4: 필터링 조건 만족
        if (CONDITION_VALIDATORS.containsKey(testCondition.getCondition())) {
            assertThat(result.getContent())
                    .allMatch(CONDITION_VALIDATORS.get(testCondition.getCondition()));
        }

        // 검증 5: 색상 조건 만족
        if (testCondition.getColorConditions() != null && !testCondition.getColorConditions().isEmpty()) {
            List<String> colorHexCodes = testCondition.getColorConditions().stream()
                    .map(ColorCondition::getHexCode)
                    .toList();
            assertThat(result.getContent())
                    .allMatch(product ->
                            colorHexCodes.stream().anyMatch(product.getSearchKeywords()::contains));
        }

        // 검증 6: 가격 조건 만족
        if (testCondition.getPriceCondition() != null) {
            for (ProductListResponse product : result.getContent()) {
                if (testCondition.getPriceCondition() == PriceCondition.CUSTOM) {
                    assertThat(product.getSellPrice())
                            .isBetween(testCondition.getCustomMinPrice(), testCondition.getCustomMaxPrice());
                } else {
                    if (testCondition.getPriceCondition().getMinPrice() != null) {
                        assertThat(product.getSellPrice())
                                .isGreaterThanOrEqualTo(testCondition.getPriceCondition().getMinPrice());
                    }
                    if (testCondition.getPriceCondition().getMaxPrice() != null) {
                        assertThat(product.getSellPrice())
                                .isLessThanOrEqualTo(testCondition.getPriceCondition().getMaxPrice());
                    }
                }
            }
        }

        // 검증 7: 정보 조건 만족
        if (testCondition.getInfoSearches() != null && !testCondition.getInfoSearches().isEmpty()) {
            for (InfoSearch infoSearch : testCondition.getInfoSearches()) {
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
    }

    private static final Map<OrderBy, Comparator<ProductListResponse>> ORDER_VALIDATORS = Map.of(
            OrderBy.HIGH_REVIEW, Comparator.comparing(ProductListResponse::getRvwCnt, Comparator.nullsLast(Comparator.reverseOrder())), // 리뷰 많은 순
            OrderBy.HIGH_PRICE, Comparator.comparing(ProductListResponse::getSellPrice, Comparator.nullsLast(Comparator.reverseOrder())), // 높은 가격 순
            OrderBy.LOW_PRICE, Comparator.comparing(ProductListResponse::getSellPrice, Comparator.nullsLast(Comparator.naturalOrder())) // 낮은 가격 순
    );

    private static final Map<Condition, Predicate<ProductListResponse>> CONDITION_VALIDATORS = Map.of(
            Condition.NEW, product -> product.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(1)), // 최신 상품
            Condition.BEST, product -> product.getWishCnt() >= 30, // 찜 개수 30 이상
            Condition.HANDMADE, product -> "Y".equals(product.getHandMadeYn()), // 핸드메이드 여부 확인
            Condition.DISCOUNT, product -> product.getDiscRate() > 0 // 할인율이 0보다 큰 상품
    );


    static Stream<TestCondition> provideAllConditions() {
        return Stream.of(
                new TestCondition("NEW", "티셔츠", OrderBy.HIGH_REVIEW, Condition.NEW,
                        List.of(ColorCondition.BLACK, ColorCondition.WHITE), PriceCondition.FROM_10K_TO_50K, null, null,
                        List.of(InfoSearch.FREESHIP, InfoSearch.DISCOUNT_ONLY)),
                new TestCondition("DISCOUNT", "맨투맨", OrderBy.LOW_PRICE, Condition.DISCOUNT,
                        List.of(ColorCondition.RED, ColorCondition.BLUE), PriceCondition.FROM_50K_TO_100K, null, null,
                        List.of(InfoSearch.EXCLUDE_OUT_OF_STOCK, InfoSearch.HANDMADE_ONLY))
        );
    }


    @Getter
    @AllArgsConstructor
    @ToString
    public static class TestCondition {
        private String keyword;
        private String categoryName;
        private OrderBy order;
        private Condition condition;
        private List<ColorCondition> colorConditions;
        private PriceCondition priceCondition;
        private Integer customMinPrice;
        private Integer customMaxPrice;
        private List<InfoSearch> infoSearches;
    }
}
