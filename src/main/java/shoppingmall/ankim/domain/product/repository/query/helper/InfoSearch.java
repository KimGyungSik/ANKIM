package shoppingmall.ankim.domain.product.repository.query.helper;

public enum InfoSearch {
    /**
     * 조건 없음 (기본값)
     */
    NONE,
    /**
     * 무료 배송
     */
    FREESHIP,

    /**
     * 품절 상품 제외
     */
    EXCLUDE_OUT_OF_STOCK,

    /**
     * 할인 상품만 보기
     */
    DISCOUNT_ONLY,
    /**
     * 핸드메이드 상품만 보기
     */
    HANDMADE_ONLY
}
