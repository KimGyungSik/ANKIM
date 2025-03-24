package shoppingmall.ankim.domain.product.repository.query.helper;

import com.querydsl.core.BooleanBuilder;
import shoppingmall.ankim.domain.product.entity.QProduct;

public enum PriceCondition {
    /**
     * 1만 원 이하
     */
    BELOW_10K(0, 10000) {
        @Override
        public void applyFilter(QProduct product, BooleanBuilder filterBuilder, Integer customMinPrice, Integer customMaxPrice) {
            filterBuilder.and(product.sellPrice.loe(10000));
        }
    },

    /**
     * 1만 원 ~ 5만 원
     */
    FROM_10K_TO_50K(10000, 50000) {
        @Override
        public void applyFilter(QProduct product, BooleanBuilder filterBuilder, Integer customMinPrice, Integer customMaxPrice) {
            filterBuilder.and(product.sellPrice.between(10000, 50000));
        }
    },

    /**
     * 5만 원 ~ 10만 원
     */
    FROM_50K_TO_100K(50000, 100000) {
        @Override
        public void applyFilter(QProduct product, BooleanBuilder filterBuilder, Integer customMinPrice, Integer customMaxPrice) {
            filterBuilder.and(product.sellPrice.between(50000, 100000));
        }
    },

    /**
     * 10만 원 ~ 30만 원
     */
    FROM_100K_TO_300K(100000, 300000) {
        @Override
        public void applyFilter(QProduct product, BooleanBuilder filterBuilder, Integer customMinPrice, Integer customMaxPrice) {
            filterBuilder.and(product.sellPrice.between(100000, 300000));
        }
    },

    /**
     * 30만 원 ~ 50만 원
     */
    FROM_300K_TO_500K(300000, 500000) {
        @Override
        public void applyFilter(QProduct product, BooleanBuilder filterBuilder, Integer customMinPrice, Integer customMaxPrice) {
            filterBuilder.and(product.sellPrice.between(300000, 500000));
        }
    },

    /**
     * 50만 원 ~ 100만 원
     */
    FROM_500K_TO_1M(500000, 1000000) {
        @Override
        public void applyFilter(QProduct product, BooleanBuilder filterBuilder, Integer customMinPrice, Integer customMaxPrice) {
            filterBuilder.and(product.sellPrice.between(500000, 1000000));
        }
    },

    /**
     * 100만 원 이상
     */
    ABOVE_1M(1000000, null) {
        @Override
        public void applyFilter(QProduct product, BooleanBuilder filterBuilder, Integer customMinPrice, Integer customMaxPrice) {
            filterBuilder.and(product.sellPrice.goe(1000000));
        }
    },

    /**
     * 직접 설정 (사용자 입력)
     */
    CUSTOM(null, null) {
        @Override
        public void applyFilter(QProduct product, BooleanBuilder filterBuilder, Integer customMinPrice, Integer customMaxPrice) {
            if (customMinPrice != null) {
                filterBuilder.and(product.sellPrice.goe(customMinPrice));
            }
            if (customMaxPrice != null) {
                filterBuilder.and(product.sellPrice.loe(customMaxPrice));
            }
        }
    };

    private final Integer minPrice;
    private final Integer maxPrice;

    PriceCondition(Integer minPrice, Integer maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public Integer getMinPrice() {
        return minPrice;
    }

    public Integer getMaxPrice() {
        return maxPrice;
    }

    // 필터 적용 메서드 (각 열거형이 구현)
    public abstract void applyFilter(QProduct product, BooleanBuilder filterBuilder, Integer customMinPrice, Integer customMaxPrice);
}

