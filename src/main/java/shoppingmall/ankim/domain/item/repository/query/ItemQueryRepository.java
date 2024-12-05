package shoppingmall.ankim.domain.item.repository.query;

import shoppingmall.ankim.domain.item.entity.Item;

import java.util.List;

public interface ItemQueryRepository {
    Item findItemByOptionValuesAndProduct(Long productNo, List<Long> optionValueNoList);
    List<Item> findItemsByProductNo(Long productNo);
}
