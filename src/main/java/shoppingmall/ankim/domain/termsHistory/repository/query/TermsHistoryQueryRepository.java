package shoppingmall.ankim.domain.termsHistory.repository.query;

import org.springframework.data.jpa.repository.JpaRepository;
import shoppingmall.ankim.domain.member.dto.TermsAgreementResponse;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.terms.repository.query.TermsQueryRepository;
import shoppingmall.ankim.domain.termsHistory.entity.TermsHistory;
import shoppingmall.ankim.domain.termsHistory.repository.TermsHistoryRepository;

import java.util.List;
import java.util.Optional;

public interface TermsHistoryQueryRepository {
//    Optional<TermsHistory> findByMemberNoAndTermsNoAndIsActive(Long memberId, Long termsId, String isActive);
    List<TermsAgreementResponse> findAgreedTermsByMember(Long memberNo, Long parentNo, String activeYn);
}
