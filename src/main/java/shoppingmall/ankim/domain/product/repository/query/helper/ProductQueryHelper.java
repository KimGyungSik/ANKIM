package shoppingmall.ankim.domain.product.repository.query.helper;

//import 생략

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import shoppingmall.ankim.domain.product.entity.QProduct;

import java.time.LocalDateTime;

public class ProductQueryHelper {

    /**
     * 정렬 수행
     * @param order 정렬 조건
     * @param product
     * @return
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
     * @return
     */
    public static BooleanBuilder createFilterBuilder(Condition condition, Long category, String keyword, QProduct product) {
        BooleanBuilder filterBuilder = new BooleanBuilder();

        // 조건 필터링
        addConditionFilters(condition, product, filterBuilder);
        // 카테고리 필터링
        addCategoryFilter(category, product, filterBuilder);
        // 검색 필터링
        addKeywordFilter(keyword, product, filterBuilder);

        return filterBuilder;
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

    // 검색 필터링 메서드
    // 상품명 or 검색 키워드 or 상세 설명
    private static void addKeywordFilter(String keyword, QProduct product, BooleanBuilder filterBuilder) {
        if (keyword != null) {
            filterBuilder.and(
                    product.name.containsIgnoreCase(keyword)
                            .or(product.searchKeywords.containsIgnoreCase(keyword))
                            .or(product.desc.containsIgnoreCase(keyword))
            );
        }
    }

}
