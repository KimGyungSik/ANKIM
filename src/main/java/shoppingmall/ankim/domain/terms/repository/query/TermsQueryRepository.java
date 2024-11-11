package shoppingmall.ankim.domain.terms.repository.query;

import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.terms.entity.TermsCategory;

import java.util.List;

public interface TermsQueryRepository {
    List<Terms> findAllSubTermsRecursively(TermsCategory category, String activeYn);
    List<Terms> findLevelSubTerms(TermsCategory category, Integer level, String activeYn);
    List<Terms> findSubTermsForParent(Long parentNo, Integer level, String activeYn);
}
