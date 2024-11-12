package shoppingmall.ankim.domain.option.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.option.service.request.OptionGroupCreateServiceRequest;
import shoppingmall.ankim.domain.option.service.request.OptionValueCreateServiceRequest;

import java.util.List;
import java.util.stream.Collectors;

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
    public OptionGroupCreateServiceRequest toServiceRequest() {
        List<OptionValueCreateServiceRequest> optionValueServiceRequests = this.optionValues.stream()
                .map(OptionValueCreateRequest::toServiceRequest)
                .collect(Collectors.toList());

        return OptionGroupCreateServiceRequest.builder()
                .groupName(this.groupName)
                .optionValues(optionValueServiceRequests)
                .build();
    }
}
