package shoppingmall.ankim.domain.option.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class OptionGroupCreateRequest {
    private String groupName;
    private List<OptionValueCreateRequest> optionValues;

    @Builder
    private OptionGroupCreateRequest(String groupName, List<OptionValueCreateRequest> optionValues) {
        this.groupName = groupName;
        this.optionValues = optionValues;
    }
}
