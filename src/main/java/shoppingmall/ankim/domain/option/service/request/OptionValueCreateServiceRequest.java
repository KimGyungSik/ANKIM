package shoppingmall.ankim.domain.option.service.request;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OptionValueCreateServiceRequest {
    private String valueName;
    private String colorCode;
    @Builder
    private OptionValueCreateServiceRequest(String valueName, String colorCode) {
        this.valueName = valueName;
        this.colorCode = colorCode;
    }
}
