package shoppingmall.ankim.domain.product.repository.query.helper;

import com.querydsl.core.BooleanBuilder;
import shoppingmall.ankim.domain.product.entity.QProduct;

public enum InfoSearch {
    /**
     * 조건 없음 (기본값)
     */
    NONE {
        @Override
        public void applyFilter(QProduct product, BooleanBuilder filterBuilder) {
            // 기본적으로 아무런 필터링도 적용하지 않음.
        }
    },

    /**
     * 무료 배송
     */
    FREESHIP {
        @Override
        public void applyFilter(QProduct product, BooleanBuilder filterBuilder) {
            filterBuilder.and(product.freeShip.eq("Y"));
        }
    },

    /**
     * 품절 상품 제외
     */
    EXCLUDE_OUT_OF_STOCK {
        @Override
        public void applyFilter(QProduct product, BooleanBuilder filterBuilder) {
            filterBuilder.and(product.qty.gt(0));
        }
    },

    /**
     * 할인 상품만 보기
     */
    DISCOUNT_ONLY {
        @Override
        public void applyFilter(QProduct product, BooleanBuilder filterBuilder) {
            filterBuilder.and(product.discRate.gt(0));
        }
    },

    /**
     * 핸드메이드 상품만 보기
     */
    HANDMADE_ONLY {
        @Override
        public void applyFilter(QProduct product, BooleanBuilder filterBuilder) {
            filterBuilder.and(product.handMadeYn.eq("Y"));
        }
    };

    // 각 Enum이 필터링 조건을 직접 구현
    public abstract void applyFilter(QProduct product, BooleanBuilder filterBuilder);
}
