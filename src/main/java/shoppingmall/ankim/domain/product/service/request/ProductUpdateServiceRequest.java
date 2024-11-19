package shoppingmall.ankim.domain.product.service.request;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.image.service.request.ProductImgCreateServiceRequest;
import shoppingmall.ankim.domain.image.service.request.ProductImgUpdateServiceRequest;
import shoppingmall.ankim.domain.item.service.request.ItemCreateServiceRequest;
import shoppingmall.ankim.domain.item.service.request.ItemUpdateServiceRequest;
import shoppingmall.ankim.domain.option.service.request.OptionGroupCreateServiceRequest;
import shoppingmall.ankim.domain.option.service.request.OptionGroupUpdateServiceRequest;

import java.util.List;

@Getter
@NoArgsConstructor
public class ProductUpdateServiceRequest implements CategoryRequest{

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

    private Long categoryNo; // 소분류 카테고리 ID

    private List<OptionGroupUpdateServiceRequest> optionGroups; // 옵션 그룹 리스트
    private ProductImgUpdateServiceRequest productImages; // 상품 이미지 리스트
    private ItemUpdateServiceRequest items;


    @Builder
    public ProductUpdateServiceRequest(String name, String desc, Integer discRate, Integer origPrice, String optYn,
                                       String restockYn, Integer qty, String bestYn, String freeShip, Integer shipFee,
                                       String searchKeywords, String cauProd, String cauOrd, String cauShip,
                                       Long categoryNo, List<OptionGroupUpdateServiceRequest> optionGroups,
                                       ProductImgUpdateServiceRequest productImages,
                                       ItemUpdateServiceRequest items) {
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
        this.categoryNo = categoryNo;
        this.optionGroups = optionGroups;
        this.productImages = productImages;
        this.items = items;
    }
}

