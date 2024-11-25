package shoppingmall.ankim.domain.item.service.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.product.entity.ProductSellingStatus;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDetailServiceRequest {
    private String name; // 품목명 (예: "컬러: Blue, 사이즈: Large")
    private List<String> optionValueNames; // 옵션 값 이름 리스트 (예: ["Blue", "Large"])
    private Integer addPrice; // 추가 금액
    private Integer qty; // 재고량
    private Integer safQty; // 안전 재고량
    private ProductSellingStatus sellingStatus; // 판매 상태
    private Integer maxQty; // 최대 구매 수량
    private Integer minQty; // 최소 구매 수량
}

