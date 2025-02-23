package shoppingmall.ankim.domain.itemOption.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.option.entity.OptionValue;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(ItemOptionId.class)
public class ItemOption {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_no", nullable = false)
    private Item item;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "optv_no", nullable = false)
    private OptionValue optionValue;

    public ItemOption(Item item, OptionValue optionValue) {
        this.item = item;
        this.optionValue = optionValue;
    }
}
