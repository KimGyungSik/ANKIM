package shoppingmall.ankim.domain.product.repository.query.helper;

//import 생략

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import shoppingmall.ankim.domain.product.entity.QProduct;

import java.time.LocalDateTime;
import java.util.List;

// TODO Enum -> 상수별 메서드 구현 사용하여 리팩토링 해볼것 (이펙티브 자바 Item 34 )

public class ProductQueryHelper {

    /**
     * 정렬 수행
     * @param order 정렬 조건
     * @param product
     * @return OrderSpecifier
     */
    public static OrderSpecifier<?> getOrderSpecifier(OrderBy order, QProduct product) {
        if (order == null) {
            // order가 null인 경우 기본 정렬 기준으로 처리
            return product.createdAt.desc();
        }
        return switch (order) {
            case POPULAR -> product.wishCnt.desc();
            case LOW_PRICE -> product.sellPrice.asc();
            case HIGH_PRICE -> product.sellPrice.desc();
            case HIGH_DISCOUNT_RATE -> product.discRate.desc();
            case HIGH_REVIEW -> product.rvwCnt.desc();
            case HIGH_VIEW -> product.viewCnt.desc();
            default -> product.createdAt.desc();
        };
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
            // 조건이 없으면 필터를 추가하지 않음
            return;
        }

        for (InfoSearch infoSearch : infoSearches) {
            addProductInfoFilter(infoSearch, product, filterBuilder);
        }
    }


    private static void addProductInfoFilter(InfoSearch infoSearch, QProduct product, BooleanBuilder filterBuilder) {
        if (infoSearch == null || infoSearch == InfoSearch.NONE) {
            // 조건이 없으면 필터를 추가하지 않음
            return;
        }

        switch (infoSearch) {
            case FREESHIP:
                filterBuilder.and(product.freeShip.eq("Y")); // 무료배송 여부가 "Y"인 상품
                break;
            case EXCLUDE_OUT_OF_STOCK:
                filterBuilder.and(product.qty.gt(0)); // 재고가 0보다 큰 상품
                break;
            case DISCOUNT_ONLY:
                filterBuilder.and(product.discRate.gt(0)); // 할인율이 0보다 큰 상품
                break;
            case HANDMADE_ONLY:
                filterBuilder.and(product.handMadeYn.eq("Y")); // 핸드메이드 여부가 "Y"인 상품
                break;
            default:
                // 기본적으로 조건 없음 (NONE)
                break;
        }
    }


    private static void addPriceFilter(PriceCondition priceCondition, Integer customMinPrice, Integer customMaxPrice, QProduct product, BooleanBuilder filterBuilder) {
        if (priceCondition == null) {
            return;
        }

        if (priceCondition == PriceCondition.CUSTOM) {
            // 사용자 정의 가격 필터링
            if (customMinPrice != null) {
                filterBuilder.and(product.sellPrice.goe(customMinPrice));
            }
            if (customMaxPrice != null) {
                filterBuilder.and(product.sellPrice.loe(customMaxPrice));
            }
        } else {
            // 고정된 가격 조건 필터링
            if (priceCondition.getMinPrice() != null) {
                filterBuilder.and(product.sellPrice.goe(priceCondition.getMinPrice()));
            }
            if (priceCondition.getMaxPrice() != null) {
                filterBuilder.and(product.sellPrice.loe(priceCondition.getMaxPrice()));
            }
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
            // 조건이 없으면 필터를 추가하지 않음
            return;
        }

        // 색상 필터 추가
        String colorHexCode = colorCondition.getHexCode(); // ColorCondition에서 Hex 색상 코드 가져오기
        if (colorHexCode != null) {
            filterBuilder.and(product.searchKeywords.contains(colorHexCode)); // 검색 키워드에 색상 코드가 포함되어 있는지 필터링
        }
    }

    // 조건 필터링 메서드
    private static void addConditionFilters(Condition condition, QProduct product, BooleanBuilder filterBuilder) {
        if (condition != null) {
            switch (condition) {
                case NEW:
                    filterBuilder.and(product.createdAt.after(LocalDateTime.now().minusMonths(1)));
                    break;
                case BEST:
                    filterBuilder.and(product.wishCnt.goe(30L));
                    break;
                case HANDMADE:
                    filterBuilder.and(product.handMadeYn.eq("Y"));
                    break;
                case DISCOUNT:
                    filterBuilder.and(product.discRate.gt(0L));
                    break;
                default:
                    if (condition.isCategoryCondition()) {
                        // 카테고리 필터링 메서드 호출
                        addCategoryFilter(
                                condition.getCategoryName(), // 카테고리 이름으로 필터링
                                product,
                                filterBuilder
                        );
                    }
                    break;
            }
        }
    }

    // 메뉴바(중분류) 카테고리 필터링 메서드
    private static void addCategoryFilter(String categoryName, QProduct product, BooleanBuilder filterBuilder) {
        if (categoryName != null) {
            filterBuilder.andAnyOf(
                    product.category.name.eq(categoryName),
                    product.category.parent.name.eq(categoryName)
            );
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
