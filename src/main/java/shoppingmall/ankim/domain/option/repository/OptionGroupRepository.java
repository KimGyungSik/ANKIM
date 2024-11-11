package shoppingmall.ankim.domain.option.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingmall.ankim.domain.option.entity.OptionGroup;

public interface OptionGroupRepository extends JpaRepository<OptionGroup,Long> {
}
