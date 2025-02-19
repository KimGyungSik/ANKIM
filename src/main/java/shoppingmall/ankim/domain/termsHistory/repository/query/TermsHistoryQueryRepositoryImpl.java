package shoppingmall.ankim.domain.termsHistory.repository.query;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shoppingmall.ankim.domain.member.dto.TermsAgreementResponse;
import shoppingmall.ankim.domain.terms.entity.QTerms;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.terms.repository.TermsRepository;
import shoppingmall.ankim.domain.termsHistory.entity.QTermsHistory;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TermsHistoryQueryRepositoryImpl implements TermsHistoryQueryRepository {

    private final JPAQueryFactory queryFactory;

    private final TermsRepository termsRepository; // Terms 쿼리 사용

    /**
     * 특정 부모 약관과 하위 약관을 포함한 약관 목록 조회 후, 사용자의 동의 이력을 JOIN
     */
    @Override
    public List<TermsAgreementResponse> findAgreedTermsByMember(Long memberNo, Long parentNo, String activeYn) {
        QTerms terms = QTerms.terms;
        QTermsHistory termsHistory = QTermsHistory.termsHistory;

        // 상하위 약관 조회
        List<Terms> termsList = termsRepository.findSubTermsIncludingParent(parentNo, 1, activeYn);
        List<Long> termsNoList = termsList.stream().map(Terms::getNo).toList(); // 약관 번호 리스트 추출

        return queryFactory
                .select(Projections.constructor(TermsAgreementResponse.class,
                        terms.no,
                        terms.name,
                        terms.contents,
                        terms.termsYn,
                        termsHistory.no,
                        termsHistory.agreeYn
                ))
                .from(terms)
                .leftJoin(termsHistory).on(terms.no.eq(termsHistory.terms.no)
                        .and(termsHistory.member.no.eq(memberNo))) // 사용자에 해당하는 동의 내역만
                .where(terms.no.in(termsNoList)) // 부모 및 하위 약관 포함
                .orderBy(terms.level.asc())
                .fetch();
    }
}