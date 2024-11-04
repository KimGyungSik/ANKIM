package shoppingmall.ankim.domain.itemOption.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@IdClass(ItemOptionId.class)
@Table(name = "ItemOption")
public class ItemOption {
    @Id
    private Long itemNo;

    @Id
    private Long optvNo;
}