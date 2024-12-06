package shoppingmall.ankim.domain.termsHistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.termsHistory.entity.TermsHistory;
import shoppingmall.ankim.domain.termsHistory.repository.query.TermsHistoryQueryRepository;

import java.util.Optional;

public interface TermsHistoryRepository extends JpaRepository<Terms, Long>, TermsHistoryQueryRepository {

    @Query("SELECT th FROM TermsHistory th WHERE th.member.no = :memberNo AND th.terms.no = :termsNo AND th.activeYn = 'Y'")
    Optional<TermsHistory> findByMemberAndTerms(@Param("memberNo") Long memberNo, @Param("termsNo") Long termsNo);

}
