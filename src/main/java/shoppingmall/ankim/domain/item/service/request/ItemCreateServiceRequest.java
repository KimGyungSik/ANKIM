package shoppingmall.ankim.domain.item.service.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemCreateServiceRequest {
    private Integer addPrice; // 추가금액
    private Integer qty; // 재고량
    private Integer safQty; // 안전재고량
    private Integer maxQty; // 최대 구매 수량
    private Integer minQty; // 최소 구매 수량

    @Builder
    private ItemCreateServiceRequest(Integer addPrice, Integer qty, Integer safQty, Integer maxQty, Integer minQty) {
        this.addPrice = addPrice;
        this.qty = qty;
        this.safQty = safQty;
        this.maxQty = maxQty;
        this.minQty = minQty;
    }
}
