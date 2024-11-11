package shoppingmall.ankim.domain.option.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
