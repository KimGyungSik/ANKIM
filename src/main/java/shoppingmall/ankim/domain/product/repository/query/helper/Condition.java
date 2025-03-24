package shoppingmall.ankim.domain.product.repository.query.helper;

import com.querydsl.core.BooleanBuilder;
import shoppingmall.ankim.domain.product.entity.QProduct;

import java.time.LocalDateTime;

public enum Condition {
    // 1차 필터링 : 최신 한달 이내 등록 상품 / 베스트 상품 / 핸드메이드 상품 / 할인 중인 상품 / 중분류 카테고리별 상품
    NEW(null) {
        @Override
        public void applyFilter(QProduct product, BooleanBuilder filterBuilder) {
            filterBuilder.and(product.createdAt.after(LocalDateTime.now().minusMonths(1)));
        }
    },
    BEST(null) {
        @Override
        public void applyFilter(QProduct product, BooleanBuilder filterBuilder) {
            filterBuilder.and(product.wishCnt.goe(30L));
        }
    },
    HANDMADE(null) {
        @Override
        public void applyFilter(QProduct product, BooleanBuilder filterBuilder) {
            filterBuilder.and(product.handMadeYn.eq("Y"));
        }
    },
    DISCOUNT(null) {
        @Override
        public void applyFilter(QProduct product, BooleanBuilder filterBuilder) {
            filterBuilder.and(product.discRate.gt(0L));
        }
    },
    BOTTOM("BOTTOM"),
    KNIT("KNIT"),
    OUTER("OUTER"),
    TOP("TOP"),
    SHIRT("SHIRT"),
    OPS("OPS/SK");

    private final String categoryName;

    // 카테고리 기반 필터 추가
    Condition(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public boolean isCategoryCondition() {
        return categoryName != null;
    }

    // 카테고리 필터의 기본 구현 (카테고리명 기준)
    public void applyFilter(QProduct product, BooleanBuilder filterBuilder) {
        if (isCategoryCondition()) {
            filterBuilder.andAnyOf(
                    product.category.name.eq(categoryName),
                    product.category.parent.name.eq(categoryName)
            );
        }
    }
}

