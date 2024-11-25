package shoppingmall.ankim.domain.option.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.option.service.request.OptionGroupUpdateServiceRequest;
import shoppingmall.ankim.domain.option.service.request.OptionValueUpdateServiceRequest;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class OptionGroupUpdateRequest {
    private Long groupId; // 수정할 그룹 ID
    private String groupName; // 그룹명
    private List<OptionValueUpdateRequest> optionValues; // 옵션 값 수정 요청 리스트

    @Builder
    private OptionGroupUpdateRequest(Long groupId, String groupName, List<OptionValueUpdateRequest> optionValues) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.optionValues = optionValues;
    }

    public OptionGroupUpdateServiceRequest toServiceRequest() {
        List<OptionValueUpdateServiceRequest> optionValueServiceRequests = this.optionValues.stream()
                .map(OptionValueUpdateRequest::toServiceRequest)
                .collect(Collectors.toList());

        return OptionGroupUpdateServiceRequest.builder()
                .groupId(this.groupId)
                .groupName(this.groupName)
                .optionValues(optionValueServiceRequests)
                .build();
    }
}