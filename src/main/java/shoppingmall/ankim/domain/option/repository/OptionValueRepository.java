package shoppingmall.ankim.domain.option.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shoppingmall.ankim.domain.option.entity.OptionValue;

import java.util.List;
import java.util.Optional;

public interface OptionValueRepository extends JpaRepository<OptionValue,Long> {
    @Query("SELECT ov FROM OptionValue ov JOIN FETCH ov.optionGroup WHERE ov.no = :optionValueId")
    Optional<OptionValue> findOptionWithGroupById(@Param("optionValueId") Long optionValueId);

    @Query("SELECT ov FROM OptionValue ov WHERE ov.no IN :ids")
    List<OptionValue> findByNoIn(@Param("ids") List<Long> ids);

    // 특정 옵션 그룹에 속한 옵션 값 이름 리스트로 조회
    List<OptionValue> findByOptionGroupNoAndNameIn(Long optionGroupId, List<String> names);
}
