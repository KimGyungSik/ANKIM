package shoppingmall.ankim.domain.option.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.option.service.request.OptionValueUpdateServiceRequest;

@Data
@NoArgsConstructor
public class OptionValueUpdateRequest {
    private Long valueId; // 수정할 값 ID
    private String valueName; // 옵션 값 이름
    private String colorCode; // 옵션 색상 코드

    @Builder
    private OptionValueUpdateRequest(Long valueId, String valueName, String colorCode) {
        this.valueId = valueId;
        this.valueName = valueName;
        this.colorCode = colorCode;
    }

    public OptionValueUpdateServiceRequest toServiceRequest() {
        return OptionValueUpdateServiceRequest.builder()
                .valueId(this.valueId)
                .valueName(this.valueName)
                .colorCode(this.colorCode)
                .build();
    }
}