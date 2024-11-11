package shoppingmall.ankim.domain.itemOption.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingmall.ankim.domain.itemOption.entity.ItemOption;

public interface ItemOptionRepository extends JpaRepository<ItemOption,Long> {
}
