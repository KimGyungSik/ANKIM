package shoppingmall.ankim.domain.option.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.option.service.request.OptionValueCreateServiceRequest;

@Data
@NoArgsConstructor
public class OptionValueCreateRequest {
    private String valueName;
    private String colorCode;
    @Builder
    private OptionValueCreateRequest(String valueName, String colorCode) {
        this.valueName = valueName;
        this.colorCode = colorCode;
    }
    public OptionValueCreateServiceRequest toServiceRequest() {
        return OptionValueCreateServiceRequest.builder()
                .valueName(this.valueName)
                .colorCode(this.colorCode)
                .build();
    }
}
