package shoppingmall.ankim.domain.option.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.option.entity.OptionValue;

@Getter
@NoArgsConstructor
public class OptionValueResponse {
    private Long optionValueNo;
    private String valueName;
    private String colorCode;

    @Builder
    private OptionValueResponse(Long optionValueNo,String valueName, String colorCode) {
        this.optionValueNo = optionValueNo;
        this.valueName = valueName;
        this.colorCode = colorCode;
    }

    // 엔티티를 DTO로 변환하는 정적 메서드
    public static OptionValueResponse of(OptionValue optionValue) {
        return OptionValueResponse.builder()
                .optionValueNo(optionValue.getNo())
                .valueName(optionValue.getName())
                .colorCode(optionValue.getColorCode())
                .build();
    }
}
