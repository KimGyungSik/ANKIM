package shoppingmall.ankim.domain.itemOption.entity;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class ItemOptionId implements Serializable {
    private Long item;
    private Long optionValue;

    public ItemOptionId(Long item, Long optionValue) {
        this.item = item;
        this.optionValue = optionValue;
    }
}


