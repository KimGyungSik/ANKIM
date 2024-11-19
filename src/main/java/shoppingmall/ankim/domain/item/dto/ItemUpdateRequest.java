package shoppingmall.ankim.domain.item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.item.service.request.ItemDetailServiceRequest;
import shoppingmall.ankim.domain.item.service.request.ItemUpdateServiceRequest;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ItemUpdateRequest {
    @NotNull(message = "품목 정보는 필수 입력 값입니다.")
    private List<ItemDetailRequest> items; // 옵션 조합별 상세 요청 리스트

    @Builder
    public ItemUpdateRequest(List<ItemDetailRequest> items) {
        this.items = items;
    }

    // 서비스 요청 객체로 변환
    public ItemUpdateServiceRequest toServiceRequest() {
        List<ItemDetailServiceRequest> serviceRequests = items.stream()
                .map(ItemDetailRequest::toServiceRequest)
                .collect(Collectors.toList());
        return ItemUpdateServiceRequest.builder()
                .items(serviceRequests)
                .build();
    }
}
