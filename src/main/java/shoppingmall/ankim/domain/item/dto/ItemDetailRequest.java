package shoppingmall.ankim.domain.item.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.item.service.request.ItemCreateServiceRequest;
import shoppingmall.ankim.domain.item.service.request.ItemDetailServiceRequest;

import java.util.List;

@Getter
@NoArgsConstructor
public class ItemDetailRequest {
    @NotNull(message = "품목명은 필수 입력 값입니다.")
    private String name; // 품목명 (예: "컬러: Blue, 사이즈: Large")

    @NotNull(message = "옵션 값 이름 리스트는 필수 입력 값입니다.")
    private List<String> optionValueNames; // 옵션 값 이름 리스트 (예: ["Blue", "Large"])

    @PositiveOrZero(message = "추가 금액은 0 이상이어야 합니다.")
    private Integer addPrice; // 추가 금액

    @NotNull(message = "재고량은 필수 입력 값입니다.")
    private Integer qty; // 재고량

    @NotNull(message = "안전 재고량은 필수 입력 값입니다.")
    private Integer safQty; // 안전 재고량

    @NotNull(message = "최대 구매 수량은 필수 입력 값입니다.")
    private Integer maxQty; // 최대 구매 수량

    @NotNull(message = "최소 구매 수량은 필수 입력 값입니다.")
    private Integer minQty; // 최소 구매 수량

    @Builder
    public ItemDetailRequest(String name, List<String> optionValueNames, Integer addPrice, Integer qty, Integer safQty, Integer maxQty, Integer minQty) {
        this.name = name;
        this.optionValueNames = optionValueNames;
        this.addPrice = addPrice;
        this.qty = qty;
        this.safQty = safQty;
        this.maxQty = maxQty;
        this.minQty = minQty;
    }

    /**
     * 컨트롤러에서 사용하는 DTO를 서비스 계층의 DTO로 변환
     */
    public ItemDetailServiceRequest toServiceRequest() {
        return ItemDetailServiceRequest.builder()
                .name(name)
                .optionValueNames(optionValueNames)
                .addPrice(addPrice)
                .qty(qty)
                .safQty(safQty)
                .maxQty(maxQty)
                .minQty(minQty)
                .build();
    }
}


