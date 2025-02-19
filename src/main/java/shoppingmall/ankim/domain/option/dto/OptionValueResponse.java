package shoppingmall.ankim.domain.option.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.option.entity.OptionValue;

import java.util.Objects;

@Getter
@NoArgsConstructor
public class OptionValueResponse {
    private Long optionValueNo;
    private String valueName;
    private String colorCode;
    private Long optionGroupNo;
    private Long itemId;

    @Builder
    private OptionValueResponse(Long optionValueNo,String valueName, String colorCode, Long optionGroupNo, Long itemId) {
        this.optionValueNo = optionValueNo;
        this.valueName = valueName;
        this.colorCode = colorCode;
        this.optionGroupNo = optionGroupNo;
        this.itemId = itemId;
    }

    // 엔티티를 DTO로 변환하는 정적 메서드
    public static OptionValueResponse of(OptionValue optionValue) {
        return OptionValueResponse.builder()
                .optionValueNo(optionValue.getNo())
                .valueName(optionValue.getName())
                .colorCode(optionValue.getColorCode())
                .optionGroupNo(optionValue.getOptionGroup().getNo())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OptionValueResponse that = (OptionValueResponse) o;
        return Objects.equals(optionValueNo, that.optionValueNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(optionValueNo);
    }
}
