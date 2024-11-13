package shoppingmall.ankim.domain.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingmall.ankim.domain.item.entity.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item,Long> {
    List<Item> findByProduct_No(Long prodNo);
}
