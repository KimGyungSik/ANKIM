package shoppingmall.ankim.domain.terms.repository.query;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shoppingmall.ankim.domain.terms.dto.TermsJoinResponse;
import shoppingmall.ankim.domain.terms.entity.QTerms;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.terms.entity.TermsCategory;

import java.util.ArrayList;
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
    public List<TermsJoinResponse> findLevelSubTerms(TermsCategory category, Integer level, String activeYn) {
        QTerms terms = QTerms.terms;

        return queryFactory
                .select(Projections.fields(TermsJoinResponse.class,
                        terms.no,
                        terms.name,
                        terms.contents,
                        terms.termsYn,
                        terms.level))
                .from(terms)
//                .leftJoin(terms.subTerms)
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

    @Override
    public List<Terms> findSubTermsIncludingParent(Long parentNo, Integer level, String activeYn) {
        QTerms terms = QTerms.terms;

        // 상위 약관과 하위 약관을 포함한 리스트를 반환
        return queryFactory
                .selectFrom(terms)
                .where(
                        terms.no.eq(parentNo).or(terms.parentTerms.no.eq(parentNo))
                                .and(terms.activeYn.eq(activeYn))
                                .and(terms.level.goe(level))  // 주어진 레벨 이상의 하위 약관만 조회
                )
                .orderBy(terms.level.asc())
                .fetch();
    }

    @Override
    public List<Terms> findAllSubTermsIncludingParent(Long parentNo, String activeYn) {
        List<Terms> allTerms = new ArrayList<>();
        fetchSubTermsRecursive(parentNo, activeYn, allTerms);
        return allTerms;
    }

    private void fetchSubTermsRecursive(Long parentNo, String activeYn, List<Terms> allTerms) {
        QTerms terms = QTerms.terms;

        // 상위 및 직속 하위 약관 조회
        List<Terms> subTerms = queryFactory
                .selectFrom(terms)
                .where(
                        terms.parentTerms.no.eq(parentNo)
                                .and(terms.activeYn.eq(activeYn))
                )
                .orderBy(terms.level.asc())
                .fetch();

        // 결과 추가
        allTerms.addAll(subTerms);

        // 하위 약관의 하위 약관 탐색
        for (Terms subTerm : subTerms) {
            fetchSubTermsRecursive(subTerm.getNo(), activeYn, allTerms);
        }
    }
}