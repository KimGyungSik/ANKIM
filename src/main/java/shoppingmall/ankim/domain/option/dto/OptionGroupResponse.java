package shoppingmall.ankim.domain.option.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.option.entity.OptionGroup;
import shoppingmall.ankim.domain.option.entity.OptionValue;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class OptionGroupResponse {
    private Long optionGroupNo;
    private String groupName;
    private List<OptionValueResponse> optionValueResponses;

    @Builder
    private OptionGroupResponse(Long optionGroupNo,String groupName, List<OptionValueResponse> optionValueResponses) {
        this.optionGroupNo = optionGroupNo;
        this.groupName = groupName;
        this.optionValueResponses = optionValueResponses;
    }

    // 엔티티를 DTO로 변환하는 정적 메서드
    public static OptionGroupResponse of(OptionGroup optionGroup) {
        List<OptionValueResponse> optionValueResponses = optionGroup.getOptionValues().stream()
                .map(OptionValueResponse::of)
                .collect(Collectors.toList());

        return OptionGroupResponse.builder()
                .optionGroupNo(optionGroup.getNo())
                .groupName(optionGroup.getName())
                .optionValueResponses(optionValueResponses)
                .build();
    }
}

