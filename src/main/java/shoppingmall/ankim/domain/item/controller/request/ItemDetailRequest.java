package shoppingmall.ankim.domain.item.controller.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.item.service.request.ItemCreateServiceRequest;
import shoppingmall.ankim.domain.item.service.request.ItemDetailServiceRequest;
import shoppingmall.ankim.domain.product.entity.ProductSellingStatus;

import java.util.List;

@Getter
@NoArgsConstructor
public class ItemDetailRequest {
    @NotNull(message = "항목명은 필수 입력 값입니다.") // 필수
    private String name;

    @NotNull(message = "옵션 값 이름 리스트는 필수 입력 값입니다.") // 필수
    private List<String> optionValueNames;

    @PositiveOrZero(message = "추가 금액은 0 이상이어야 합니다.") // 0 이상
    private Integer addPrice;

    @NotNull(message = "재고량은 필수 입력 값입니다.") // 필수
    @PositiveOrZero(message = "재고량은 0 이상이어야 합니다.") // 0 이상
    private Integer qty;

    @NotNull(message = "안전 재고량은 필수 입력 값입니다.") // 필수
    @PositiveOrZero(message = "안전 재고량은 0 이상이어야 합니다.") // 0 이상
    private Integer safQty;

    @NotNull(message = "품목 판매상태는 필수 입력 값입니다.") // 필수
    private ProductSellingStatus sellingStatus;

    @NotNull(message = "최대 구매 수량은 필수 입력 값입니다.") // 필수
    @PositiveOrZero(message = "최대 구매 수량은 0 이상이어야 합니다.") // 0 이상
    private Integer maxQty;

    @NotNull(message = "최소 구매 수량은 필수 입력 값입니다.") // 필수
    @PositiveOrZero(message = "최소 구매 수량은 0 이상이어야 합니다.") // 0 이상
    private Integer minQty;

    @Builder
    public ItemDetailRequest(String name, List<String> optionValueNames, Integer addPrice, Integer qty, Integer safQty,
                             ProductSellingStatus sellingStatus, Integer maxQty, Integer minQty) {
        this.name = name;
        this.optionValueNames = optionValueNames;
        this.addPrice = addPrice;
        this.qty = qty;
        this.safQty = safQty;
        this.sellingStatus = sellingStatus == null ? ProductSellingStatus.SELLING : sellingStatus;
        this.maxQty = maxQty;
        this.minQty = minQty;
    }

    public ItemDetailServiceRequest toServiceRequest() {
        return ItemDetailServiceRequest.builder()
                .name(name)
                .optionValueNames(optionValueNames)
                .addPrice(addPrice)
                .qty(qty)
                .safQty(safQty)
                .sellingStatus(sellingStatus)
                .maxQty(maxQty)
                .minQty(minQty)
                .build();
    }
}



