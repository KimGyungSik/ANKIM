package shoppingmall.ankim.domain.terms.repository.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shoppingmall.ankim.domain.terms.entity.QTerms;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.terms.entity.TermsCategory;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TermsQueryRepositoryImpl implements TermsQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Terms> findAllSubTermsRecursively(TermsCategory category, String activeYn) {
        QTerms terms = QTerms.terms;

        return queryFactory
                .selectFrom(terms)
                .leftJoin(terms.subTerms).fetchJoin()  // 하위 약관 포함
                .where(
                        terms.category.eq(category)
                                .and(terms.activeYn.eq(activeYn))
                )
                .orderBy(terms.level.asc()) // 레벨별 정렬
                .fetch();
    }

    @Override
    public List<Terms> findLevelSubTerms(TermsCategory category, Integer level, String activeYn) {
        QTerms terms = QTerms.terms;

        return queryFactory
                .selectFrom(terms)
                .leftJoin(terms.subTerms).fetchJoin()
                .where(
                        terms.category.eq(category)
                                .and(terms.activeYn.eq(activeYn))
                                .and(terms.level.eq(level))
                )
                .orderBy(terms.level.asc())
                .fetch();
    }

    @Override
    public List<Terms> findSubTermsForParent(Long parentNo, Integer level, String activeYn) {
        QTerms terms = QTerms.terms;

        return queryFactory
                .selectFrom(terms)
                .where(
                        terms.parentTerms.no.eq(parentNo)
                                .and(terms.activeYn.eq(activeYn))
                                .and(terms.level.eq(level))
                )
                .orderBy(terms.level.asc())
                .fetch();
    }
}