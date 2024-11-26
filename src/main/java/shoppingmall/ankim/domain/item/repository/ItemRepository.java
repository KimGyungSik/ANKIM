package shoppingmall.ankim.domain.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.item.repository.query.ItemQueryRepository;
import shoppingmall.ankim.domain.product.entity.Product;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item,Long>, ItemQueryRepository {
    List<Item> findByProduct_No(Long prodNo);

    long countByProduct(Product product);
}
