package shoppingmall.ankim.domain.item.dto;

// 품목에 필요한 필드들을 정의해야할듯
// 재고수량, 옵션추가가격, 안전재고량, 최대 구매 수량, 최소 구매 수량 받아야할듯
// 품목코드, 품목명은 로직으로 박아주고

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.item.service.request.ItemCreateServiceRequest;

@Getter
@NoArgsConstructor
public class ItemCreateRequest {
    @PositiveOrZero(message = "추가금액은 0 이상이어야 합니다.")
    private Integer addPrice; // 추가금액
    @NotNull(message = "재고량은 필수 입력 값입니다.")
    private Integer qty; // 재고량
    @NotNull(message = "안전재고량은 필수 입력 값입니다.")
    private Integer safQty; // 안전재고량
    @NotNull(message = "최대 구매 수량은 필수 입력 값입니다.")
    private Integer maxQty; // 최대 구매 수량
    @NotNull(message = "최소 구매 수량은 필수 입력 값입니다.")
    private Integer minQty; // 최소 구매 수량

    @Builder
    private ItemCreateRequest(Integer addPrice, Integer qty, Integer safQty, Integer maxQty, Integer minQty) {
        this.addPrice = addPrice;
        this.qty = qty;
        this.safQty = safQty;
        this.maxQty = maxQty;
        this.minQty = minQty;
    }

    public ItemCreateServiceRequest toServiceRequest() {
        return ItemCreateServiceRequest.builder()
                .addPrice(addPrice)
                .qty(qty)
                .safQty(safQty)
                .maxQty(maxQty)
                .minQty(minQty)
                .build();
    }
}
