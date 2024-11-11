package shoppingmall.ankim.domain.option.service.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.option.dto.OptionValueCreateRequest;

import java.util.List;

@Data
@NoArgsConstructor
public class OptionGroupCreateServiceRequest {
    private String groupName;
    private List<OptionValueCreateServiceRequest> optionValues;

    @Builder
    private OptionGroupCreateServiceRequest(String groupName, List<OptionValueCreateServiceRequest> optionValues) {
        this.groupName = groupName;
        this.optionValues = optionValues;
    }
}
