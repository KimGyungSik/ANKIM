package shoppingmall.ankim.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingmall.ankim.domain.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product,Long> {
}
