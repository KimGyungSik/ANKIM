package shoppingmall.ankim.domain.termsHistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.termsHistory.repository.query.TermsHistoryQueryRepository;

public interface TermsHistoryRepository extends JpaRepository<Terms, Long>, TermsHistoryQueryRepository {
}
