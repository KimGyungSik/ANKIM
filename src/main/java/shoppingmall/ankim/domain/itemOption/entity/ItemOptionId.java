package shoppingmall.ankim.domain.itemOption.entity;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class ItemOptionId implements Serializable {
    private Long item; // ItemOption 엔티티의 필드와 동일
    private Long optionValue; // ItemOption 엔티티의 필드와 동일

    public ItemOptionId(Long item, Long optionValue) {
        this.item = item;
        this.optionValue = optionValue;
    }
}
