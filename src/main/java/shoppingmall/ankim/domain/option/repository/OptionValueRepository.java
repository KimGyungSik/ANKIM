package shoppingmall.ankim.domain.option.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingmall.ankim.domain.option.entity.OptionValue;

public interface OptionValueRepository extends JpaRepository<OptionValue,Long> {
}
