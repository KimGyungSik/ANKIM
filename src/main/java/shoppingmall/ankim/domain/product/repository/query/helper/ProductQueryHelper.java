package shoppingmall.ankim.domain.product.repository.query.helper;

//import 생략

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import shoppingmall.ankim.domain.product.entity.QProduct;

import java.time.LocalDateTime;
import java.util.List;

// TODO Enum -> 상수별 메서드 구현 사용하여 리팩토링 해볼것 (이펙티브 자바 Item 34 )

public class ProductQueryHelper {

    /**
     * 정렬 수행
     * @param order 정렬 조건
     * @param product 정렬할 상품 정보
     * @return OrderSpecifier
     */
    public static OrderSpecifier<?> getOrderSpecifier(OrderBy order, QProduct product) {
        return (order != null) ? order.getOrderSpecifier(product) : OrderBy.LATEST.getOrderSpecifier(product);
    }


    /**
     * 필터링 수행
     * @param condition
     * @param category
     * @param keyword
     * @return BooleanBuilder
     */
    public static BooleanBuilder createFilterBuilder(Condition condition, Long category, String keyword,
                                                     List<ColorCondition> colorConditions, PriceCondition priceCondition,Integer customMinPrice, Integer customMaxPrice,
                                                     List<InfoSearch> infoSearches, QProduct product) {
        BooleanBuilder filterBuilder = new BooleanBuilder();

        // 메뉴바 필터링
        addConditionFilters(condition, product, filterBuilder);
        // 카테고리 필터링
        addCategoryFilter(category, product, filterBuilder);
        // 검색 필터링
        addKeywordFilter(keyword, product, filterBuilder);
        // 색상 필터링 -> 특정 옵션 컬러 색상코드를 가지고 있는 상품
        addColorFilters(colorConditions, product, filterBuilder);
        // 가격 필터링 -> 가격대별 상품
        addPriceFilter(priceCondition, customMinPrice, customMaxPrice, product, filterBuilder);
        // 상품정보 필터링 -> 무료배송인 상품, 할인 상품, 품절 상품 제외,핸드메이드 상품만
        addProductInfoFilters(infoSearches, product, filterBuilder);

        return filterBuilder;
    }

    private static void addProductInfoFilters(List<InfoSearch> infoSearches, QProduct product, BooleanBuilder filterBuilder) {
        if (infoSearches == null || infoSearches.isEmpty()) {
            return;
        }

        for (InfoSearch infoSearch : infoSearches) {
            infoSearch.applyFilter(product, filterBuilder);
        }
    }

    private static void addPriceFilter(PriceCondition priceCondition, Integer customMinPrice, Integer customMaxPrice, QProduct product, BooleanBuilder filterBuilder) {
        if (priceCondition != null) {
            priceCondition.applyFilter(product, filterBuilder, customMinPrice, customMaxPrice);
        }
    }


    private static void addColorFilters(List<ColorCondition> colorConditions, QProduct product, BooleanBuilder filterBuilder) {
        if (colorConditions == null || colorConditions.isEmpty()) {
            // 조건이 없으면 필터를 추가하지 않음
            return;
        }

        for (ColorCondition colorCondition : colorConditions) {
            addColorFilter(colorCondition, product, filterBuilder);
        }
    }

    private static void addColorFilter(ColorCondition colorCondition, QProduct product, BooleanBuilder filterBuilder) {
        if (colorCondition == null) {
            return;
        }

        String colorHexCode = colorCondition.getHexCode(); // 예: "#000000"
        String colorName = colorCondition.name();           // 예: "BLACK"

        BooleanBuilder colorConditionBuilder = new BooleanBuilder();

        if (colorHexCode != null) {
            colorConditionBuilder.or(product.searchKeywords.containsIgnoreCase(colorHexCode));
        }
        colorConditionBuilder.or(
                Expressions.stringTemplate(
                        "LOWER(REPLACE(REPLACE({0}, ',', ''), ' ', ''))", product.searchKeywords
                ).contains(colorName)
        );
        filterBuilder.and(colorConditionBuilder);
    }


    // 조건 필터링 메서드
    private static void addConditionFilters(Condition condition, QProduct product, BooleanBuilder filterBuilder) {
        if (condition != null) {
            condition.applyFilter(product, filterBuilder);
        }
    }

    // 카테고리 필터링 메서드
    private static void addCategoryFilter(Long category, QProduct product, BooleanBuilder filterBuilder) {
        if (category != null) {
            filterBuilder.andAnyOf(
                    product.category.no.eq(category),
                    product.category.parent.no.eq(category)
            );
        }
    }

    // 할인율 필터링 메서드
    private static void addDiscountRateFilter(Integer discountRate, QProduct product, BooleanBuilder filterBuilder) {
        if (discountRate != null) {
            switch (discountRate) {
                case 80:
                    filterBuilder.and(product.discRate.goe(80L));
                    break;
                case 70:
                    filterBuilder.and(product.discRate.between(70L, 79L));
                    break;
                case 60:
                    filterBuilder.and(product.discRate.between(60L, 69L));
                    break;
                case 50:
                    filterBuilder.and(product.discRate.between(50L, 59L));
                    break;
                case 40:
                    filterBuilder.and(product.discRate.between(40L, 49L));
                    break;
                case 30:
                    filterBuilder.and(product.discRate.between(30L, 39L));
                    break;
                case 20:
                    filterBuilder.and(product.discRate.between(20L, 29L));
                    break;
                default:
                    filterBuilder.and(product.discRate.gt(0L));
                    break;
            }
        } else {
            filterBuilder.and(product.discRate.gt(0L));
        }
    }


    // 검색 필터링 메서드
    // 상품명 or 검색 키워드 or 상세 설명
    private static void addKeywordFilter(String keyword, QProduct product, BooleanBuilder filterBuilder) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            String[] keywords = keyword.trim().split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)|\\s+");

            BooleanBuilder keywordCondition = new BooleanBuilder();
            for (String word : keywords) {
                keywordCondition.and(
                        product.name.containsIgnoreCase(word)
                                .or(product.searchKeywords.containsIgnoreCase(word))
                                .or(product.desc.containsIgnoreCase(word))
                );
            }

            filterBuilder.and(keywordCondition);
        }
    }
}
