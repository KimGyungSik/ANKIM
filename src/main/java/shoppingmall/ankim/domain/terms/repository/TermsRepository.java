package shoppingmall.ankim.domain.terms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.terms.entity.TermsCategory;
import shoppingmall.ankim.domain.terms.repository.query.TermsQueryRepository;

import java.util.List;

public interface TermsRepository extends JpaRepository<Terms, Long>, TermsQueryRepository {

}
