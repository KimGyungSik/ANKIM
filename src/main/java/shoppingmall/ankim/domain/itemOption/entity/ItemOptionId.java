package shoppingmall.ankim.domain.itemOption.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemOptionId implements Serializable {
    private Long itemNo;
    private Long optvNo;

    public ItemOptionId(Long itemNo, Long optvNo) {
        this.itemNo = itemNo;
        this.optvNo = optvNo;
    }

    // equals와 hashCode 구현
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemOptionId that = (ItemOptionId) o;
        return Objects.equals(itemNo, that.itemNo) && Objects.equals(optvNo, that.optvNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemNo, optvNo);
    }

    // Getters, Setters (필요에 따라 추가)
}

