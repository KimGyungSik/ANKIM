package shoppingmall.ankim.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shoppingmall.ankim.domain.product.entity.Product;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.optionGroups WHERE p.no = :productId")
    Optional<Product> findByIdWithOptionGroups(@Param("productId") Long productId);
}
