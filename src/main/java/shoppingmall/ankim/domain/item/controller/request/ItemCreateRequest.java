package shoppingmall.ankim.domain.item.controller.request;

// 품목에 필요한 필드들을 정의해야할듯
// 재고수량, 옵션추가가격, 안전재고량, 최대 구매 수량, 최소 구매 수량 받아야할듯
// 품목코드, 품목명은 로직으로 박아주고

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.item.service.request.ItemCreateServiceRequest;
import shoppingmall.ankim.domain.item.service.request.ItemDetailServiceRequest;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class ItemCreateRequest {
    @NotNull(message = "품목 정보는 필수 입력 값입니다.")
    @Valid
    private List<ItemDetailRequest> items; // 옵션 조합별 상세 요청 리스트

    @Builder
    public ItemCreateRequest(List<ItemDetailRequest> items) {
        this.items = items;
    }

    // 서비스 요청 객체로 변환
    public ItemCreateServiceRequest toServiceRequest() {
        List<ItemDetailServiceRequest> serviceRequests = items.stream()
                .map(ItemDetailRequest::toServiceRequest)
                .collect(Collectors.toList());
        return ItemCreateServiceRequest.builder()
                .items(serviceRequests)
                .build();
    }
}

