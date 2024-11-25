package shoppingmall.ankim.domain.option.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingmall.ankim.domain.option.entity.OptionGroup;
import shoppingmall.ankim.domain.product.entity.Product;

import java.util.List;

public interface OptionGroupRepository extends JpaRepository<OptionGroup,Long> {
    List<OptionGroup> findAllByProduct(Product product);
}
