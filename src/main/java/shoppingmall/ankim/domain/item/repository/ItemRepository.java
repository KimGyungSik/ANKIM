package shoppingmall.ankim.domain.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingmall.ankim.domain.item.entity.Item;

public interface ItemRepository extends JpaRepository<Item,Long> {
}
