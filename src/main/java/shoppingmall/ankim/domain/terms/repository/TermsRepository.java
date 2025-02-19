package shoppingmall.ankim.domain.terms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.terms.entity.TermsCategory;
import shoppingmall.ankim.domain.terms.repository.query.TermsQueryRepository;

import java.util.List;

public interface TermsRepository extends JpaRepository<Terms, Long>, TermsQueryRepository {
    @Query("SELECT t FROM Terms t WHERE t.parentTerms.no = :parentNo AND t.activeYn = 'Y'")
    List<Terms> findAllSubTerms(@Param("parentNo") Long parentNo);

    @Query("SELECT t FROM Terms t WHERE t.parentTerms.no is null AND t.category = :category")
    List<Terms> findByParentTerms(@Param("category") TermsCategory category);

    // 이름에 특정 키워드가 포함된 약관을 조회
    @Query("SELECT t FROM Terms t WHERE t.name LIKE %:keyword%")
    List<Terms> findTermsByNameKeyword(@Param("keyword") String keyword);
}
