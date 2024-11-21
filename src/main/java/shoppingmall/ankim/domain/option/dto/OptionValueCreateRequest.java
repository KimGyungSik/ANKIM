package shoppingmall.ankim.domain.option.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.option.service.request.OptionValueCreateServiceRequest;

@Data
@NoArgsConstructor
public class OptionValueCreateRequest {
    @NotBlank(message = "옵션명은 필수 입력 값입니다.")
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
