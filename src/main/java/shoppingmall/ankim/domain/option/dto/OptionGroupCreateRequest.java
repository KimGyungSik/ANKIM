package shoppingmall.ankim.domain.option.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.option.dto.OptionValueCreateRequest;
import shoppingmall.ankim.domain.option.service.request.OptionGroupCreateServiceRequest;
import shoppingmall.ankim.domain.option.service.request.OptionValueCreateServiceRequest;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class OptionGroupCreateRequest {
    @NotBlank(message = "옵션항목명은 필수 입력 값입니다.")
    private String groupName;

    @Valid
    private List<OptionValueCreateRequest> optionValues = List.of(); // 기본값 설정

    @Builder
    public OptionGroupCreateRequest(String groupName, List<OptionValueCreateRequest> optionValues) {
        this.groupName = groupName;
        this.optionValues = optionValues != null ? optionValues : List.of(); // null일 경우 빈 리스트로 초기화
    }

    public OptionGroupCreateServiceRequest toServiceRequest() {
        // 빈 리스트 처리 덕분에 null 체크 불필요
        List<OptionValueCreateServiceRequest> optionValueServiceRequests = this.optionValues.stream()
                .map(OptionValueCreateRequest::toServiceRequest)
                .collect(Collectors.toList());

        return OptionGroupCreateServiceRequest.builder()
                .groupName(this.groupName)
                .optionValues(optionValueServiceRequests)
                .build();
    }
}
