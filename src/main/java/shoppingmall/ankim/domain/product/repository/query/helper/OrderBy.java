package shoppingmall.ankim.domain.product.repository.query.helper;

import com.querydsl.core.types.OrderSpecifier;
import shoppingmall.ankim.domain.product.entity.QProduct;

public enum OrderBy {
    /**
     * 최신순 정렬 (CreatedAt 기준 내림차순)
     */
    LATEST {
        @Override
        public OrderSpecifier<?> getOrderSpecifier(QProduct product) {
            return product.createdAt.desc();
        }
    },

    /**
     * 인기순 정렬 (wishCnt 기준 내림차순)
     */
    POPULAR {
        @Override
        public OrderSpecifier<?> getOrderSpecifier(QProduct product) {
            return product.wishCnt.desc();
        }
    },

    /**
     * 낮은 가격순 정렬 (sellPrice 기준 오름차순)
     */
    LOW_PRICE {
        @Override
        public OrderSpecifier<?> getOrderSpecifier(QProduct product) {
            return product.sellPrice.asc();
        }
    },

    /**
     * 높은 가격순 정렬 (sellPrice 기준 내림차순)
     */
    HIGH_PRICE {
        @Override
        public OrderSpecifier<?> getOrderSpecifier(QProduct product) {
            return product.sellPrice.desc();
        }
    },

    /**
     * 높은 할인율 순 정렬 (discRate 기준 내림차순)
     */
    HIGH_DISCOUNT_RATE {
        @Override
        public OrderSpecifier<?> getOrderSpecifier(QProduct product) {
            return product.discRate.desc();
        }
    },

    /**
     * 리뷰 많은순 정렬 (rvwCnt 기준 내림차순)
     */
    HIGH_REVIEW {
        @Override
        public OrderSpecifier<?> getOrderSpecifier(QProduct product) {
            return product.rvwCnt.desc();
        }
    },

    /**
     * 조회수 많은순 정렬 (viewCnt 기준 내림차순)
     */
    HIGH_VIEW {
        @Override
        public OrderSpecifier<?> getOrderSpecifier(QProduct product) {
            return product.viewCnt.desc();
        }
    };

    // 각 정렬 기준을 적용하는 추상 메서드
    public abstract OrderSpecifier<?> getOrderSpecifier(QProduct product);
}
