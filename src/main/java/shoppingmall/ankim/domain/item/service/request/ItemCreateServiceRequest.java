package shoppingmall.ankim.domain.item.service.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ItemCreateServiceRequest {
    private List<ItemDetailServiceRequest> items; // 옵션 조합별 상세 설정 리스트

    @Builder
    public ItemCreateServiceRequest(List<ItemDetailServiceRequest> items) {
        this.items = items;
    }
}
