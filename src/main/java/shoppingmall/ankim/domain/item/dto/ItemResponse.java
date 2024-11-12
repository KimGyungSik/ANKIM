package shoppingmall.ankim.domain.item.dto;

import lombok.Builder;
import lombok.Getter;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.option.dto.OptionValueResponse;
import shoppingmall.ankim.domain.product.entity.ProductSellingStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ItemResponse {
    private final Long itemId;
    private final String productName;
    private final String code; // 품목코드
    private final String name; // 품목명
    private final BigDecimal addPrice; // 추가금액
    private final Integer qty; // 재고량
    private final Integer safQty; // 안전재고량
    private final ProductSellingStatus sellingStatus; // 판매 상태
    private final Integer maxQty; // 최대 구매 수량
    private final Integer minQty; // 최소 구매 수량
    private final List<OptionValueResponse> optionValues; // 품목 옵션 리스트

    @Builder
    private ItemResponse(Long itemId, String productName, String code, String name, BigDecimal addPrice, Integer qty, Integer safQty,
                         ProductSellingStatus sellingStatus, Integer maxQty, Integer minQty, List<OptionValueResponse> optionValues ) {
        this.itemId = itemId;
        this.productName = productName;
        this.code = code;
        this.name = name;
        this.addPrice = addPrice;
        this.qty = qty;
        this.safQty = safQty;
        this.sellingStatus = sellingStatus;
        this.maxQty = maxQty;
        this.minQty = minQty;
        this.optionValues = optionValues;
    }

    public static ItemResponse of(Item item) {
        return ItemResponse.builder()
                .itemId(item.getNo())
                .productName(item.getName())
                .code(item.getCode())
                .name(item.getName())
                .addPrice(item.getAddPrice())
                .qty(item.getQty())
                .safQty(item.getSafQty())
                .sellingStatus(item.getSellingStatus())
                .maxQty(item.getMaxQty())
                .minQty(item.getMinQty())
                .optionValues(item.getItemOptions().stream()
                        .map(itemOption -> OptionValueResponse.of(itemOption.getOptionValue()))
                        .collect(Collectors.toList()))
                .build();
    }
}