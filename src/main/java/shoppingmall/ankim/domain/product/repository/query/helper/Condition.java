package shoppingmall.ankim.domain.product.repository.query.helper;

public enum Condition {
    // 1차 필터링 : 최신 한달 이내 등록 상품 / 베스트 상품 / 핸드메이드 상품 / 할인 중인 상품 / 중분류 카테고리별 상품
    NEW(null),
    BEST(null),
    HANDMADE(null),
    DISCOUNT(null),
    BOTTOM("Bottom"),
    KNIT("Knit"),
    OUTER("Outer"),
    TOP("Top"),
    SHIRT("Shirt"),
    OPS("Ops"),
    ACC("Acc");

    private final String categoryName;

    // Constructor to initialize categoryName
    Condition(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public boolean isCategoryCondition() {
        return categoryName != null;
    }
}

