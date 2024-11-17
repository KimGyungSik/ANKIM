package shoppingmall.ankim.domain.option.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.option.dto.OptionValueResponse;

/*
 * 옵션 값 정책
 * 원하는 그룹에 맞는 값 넣기 ex) 사이즈 -> small / 컬러 -> red, blue...
 */

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "option_value")
public class OptionValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "optg_no", nullable = false)
    private OptionGroup optionGroup;

    @Column(length = 255)
    private String name;

    @Column(length = 7)
    private String colorCode;

    @Builder
    private OptionValue(OptionGroup optionGroup, String name, String colorCode) {
        this.optionGroup = optionGroup;
        this.name = name;
        this.colorCode = colorCode;
    }

    public static OptionValue create(OptionGroup optionGroup, String name, String colorCode) {
        return OptionValue.builder()
                .optionGroup(optionGroup)
                .name(name)
                .colorCode(colorCode)
                .build();
    }
    public static OptionValue fromResponse(OptionValueResponse response, OptionGroup optionGroup) {
        return OptionValue.builder()
                .optionGroup(optionGroup)
                .name(response.getValueName())
                .colorCode(response.getColorCode())
                .build();
    }

    public void update(String valueName, String colorCode) {
        this.name = valueName;
        this.colorCode = colorCode;
    }
}

