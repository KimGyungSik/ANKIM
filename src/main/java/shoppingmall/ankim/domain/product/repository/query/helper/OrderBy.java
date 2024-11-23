package shoppingmall.ankim.domain.product.repository.query.helper;

public enum OrderBy {
    /**
     * 최신순 정렬 (CreatedAt 기준 내림차순)
     */
    LATEST,

    /**
     * 인기순 정렬 (wishCnt 기준 내림차순)
     */
    POPULAR,

    /**
     * 낮은 가격순 정렬 (sellPrice 기준 오름차순)
     */
    LOW_PRICE,

    /**
     * 높은 가격순 정렬 (sellPrice 기준 내림차순)
     */
    HIGH_PRICE,

    /**
     * 높은 할인율 순 정렬 (discRate 기준 내림차순)
     */
    HIGH_DISCOUNT_RATE,

    /**
     *  리뷰 많은순 정렬 ( rvwCnt 기준 내림차순)
     */
    HIGH_REVIEW,
    /**
     *  조회수 많은순 정렬 ( viewCnt 기준 내림차순)
     */
    HIGH_VIEW
}
