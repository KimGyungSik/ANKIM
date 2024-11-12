package shoppingmall.ankim.domain.option.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shoppingmall.ankim.domain.option.entity.OptionValue;

import java.util.Optional;

public interface OptionValueRepository extends JpaRepository<OptionValue,Long> {
    @Query("SELECT ov FROM OptionValue ov JOIN FETCH ov.optionGroup WHERE ov.no = :optionValueId")
    Optional<OptionValue> findOptionWithGroupById(@Param("optionValueId") Long optionValueId);
}
