package shoppingmall.ankim.domain.product.controller.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.image.dto.ProductImgUpdateRequest;
import shoppingmall.ankim.domain.image.service.request.ProductImgUpdateServiceRequest;
import shoppingmall.ankim.domain.item.controller.request.ItemUpdateRequest;
import shoppingmall.ankim.domain.item.service.request.ItemUpdateServiceRequest;
import shoppingmall.ankim.domain.option.dto.OptionGroupCreateRequest;
import shoppingmall.ankim.domain.option.dto.OptionGroupUpdateRequest;
import shoppingmall.ankim.domain.option.service.request.OptionGroupUpdateServiceRequest;
import shoppingmall.ankim.domain.product.service.request.ProductUpdateServiceRequest;

import java.util.List;

@Data
@NoArgsConstructor
public class ProductUpdateRequest {

    @NotBlank(message = "상품명은 필수 입력 값입니다.")
    private String name; // 상품명

    private String desc; // 상세설명

    @Min(value = 0, message = "할인율은 0 이상이어야 합니다.")
    @Max(value = 100, message = "할인율은 100 이하이어야 합니다.")
    private Integer discRate; // 할인율

    @PositiveOrZero(message = "정상가격은 0 이상이어야 합니다.")
    private Integer origPrice; // 정상가격

    @Pattern(regexp = "Y|N", message = "옵션 여부는 'Y' 또는 'N'이어야 합니다.")
    private String optYn; // 옵션 여부

    @Pattern(regexp = "Y|N", message = "재입고 알림 여부는 'Y' 또는 'N'이어야 합니다.")
    private String restockYn; // 재입고 알림 여부

    @PositiveOrZero(message = "재고량은 0 이상이어야 합니다.")
    private Integer qty; // 재고량 (옵션이 없는 경우 단품)

    @Pattern(regexp = "Y|N", message = "베스트 상품 여부는 'Y' 또는 'N'이어야 합니다.")
    private String bestYn; // 베스트 상품 여부

    @Pattern(regexp = "Y|N", message = "무료배송 여부는 'Y' 또는 'N'이어야 합니다.")
    private String freeShip; // 무료배송 여부

    @PositiveOrZero(message = "배송비는 0 이상이어야 합니다.")
    private Integer shipFee; // 배송비

    @Size(max = 500, message = "검색 키워드는 최대 500자까지 입력 가능합니다.")
    private String searchKeywords; // 검색 키워드

    private String cauProd; // 상품 유의사항

    private String cauOrd; // 주문 유의사항

    private String cauShip; // 배송 유의사항

    private Long categoryNo; // 소분류 카테고리 ID

    private List<OptionGroupUpdateRequest> optionGroups; // 옵션 그룹 리스트
    private ProductImgUpdateRequest productImages; // 상품 이미지 리스트
    private ItemUpdateRequest items;

    @Builder
    private ProductUpdateRequest(String name, String desc, Integer discRate, Integer origPrice, String optYn,
                                 String restockYn, Integer qty, String bestYn, String freeShip, Integer shipFee,
                                 String searchKeywords, String cauProd, String cauOrd, String cauShip,
                                 Long categoryNo, List<OptionGroupUpdateRequest> optionGroups,
                                 ProductImgUpdateRequest productImages,
                                 ItemUpdateRequest items) {
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

    public ProductUpdateServiceRequest toServiceRequest() {
        return ProductUpdateServiceRequest.builder()
                .name(this.name)
                .desc(this.desc)
                .discRate(this.discRate)
                .origPrice(this.origPrice)
                .optYn(this.optYn)
                .restockYn(this.restockYn)
                .qty(this.qty)
                .bestYn(this.bestYn)
                .freeShip(this.freeShip)
                .shipFee(this.shipFee)
                .searchKeywords(this.searchKeywords)
                .cauProd(this.cauProd)
                .cauOrd(this.cauOrd)
                .cauShip(this.cauShip)
                .categoryNo(this.categoryNo)
                .optionGroups(this.optionGroups.stream()
                        .map(OptionGroupUpdateRequest::toServiceRequest)
                        .toList())
                .productImages(this.productImages.toServiceRequest())
                .items(this.items.toServiceRequest())
                .build();
    }
}

