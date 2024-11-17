package shoppingmall.ankim.domain.product.service.request;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductUpdateServiceRequest {

    private String name; // 상품명
    private String desc; // 상세설명
    private Integer discRate; // 할인율
    private Integer origPrice; // 정상가격
    private String optYn; // 옵션 여부
    private String restockYn; // 재입고 알림 여부
    private Integer qty; // 재고량
    private String bestYn; // 베스트 상품 여부
    private String freeShip; // 무료배송 여부
    private Integer shipFee; // 배송비
    private String searchKeywords; // 검색 키워드
    private String cauProd; // 상품 유의사항
    private String cauOrd; // 주문 유의사항
    private String cauShip; // 배송 유의사항

    @Builder
    public ProductUpdateServiceRequest(String name, String desc, Integer discRate, Integer origPrice, String optYn,
                                       String restockYn, Integer qty, String bestYn, String freeShip, Integer shipFee,
                                       String searchKeywords, String cauProd, String cauOrd, String cauShip) {
        this.name = name;
        this.desc = desc;
        this.discRate = discRate;
        this.origPrice = origPrice;
        this.optYn = optYn;
        this.restockYn = restockYn;
        this.qty = qty;
        this.bestYn = bestYn;
        this.freeShip = freeShip;
        this.shipFee = shipFee;
        this.searchKeywords = searchKeywords;
        this.cauProd = cauProd;
        this.cauOrd = cauOrd;
        this.cauShip = cauShip;
    }
}

