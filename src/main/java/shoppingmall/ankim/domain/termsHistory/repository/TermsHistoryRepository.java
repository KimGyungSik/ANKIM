package shoppingmall.ankim.domain.termsHistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.terms.entity.TermsCategory;
import shoppingmall.ankim.domain.termsHistory.entity.TermsHistory;
import shoppingmall.ankim.domain.termsHistory.repository.query.TermsHistoryQueryRepository;

import java.util.List;
import java.util.Optional;

public interface TermsHistoryRepository extends JpaRepository<TermsHistory, Long>, TermsHistoryQueryRepository {

    // 특정 회원의 약관 동의 이력 중 활성화된 동의 이력을 조회한다.
    @Query("SELECT th FROM TermsHistory th " +
            "WHERE th.member.no = :memberNo " +
            "AND th.terms.no = :termsNo " +
            "AND th.activeYn = 'Y'")
    Optional<TermsHistory> findByMemberAndTerms(@Param("memberNo") Long memberNo, @Param("termsNo") Long termsNo);

    // 특정 회원이 특정한 약관에 대해 동의한 상태(activate_yn = 'Y')인지 확인한다.
    @Query("SELECT COUNT(th) > 0 FROM TermsHistory th " +
            "WHERE th.member.no = :memberNo " +
            "AND th.terms.no = :termsNo " +
            "AND th.agreeYn = 'Y'" +
            "AND th.activeYn = 'Y'")
    boolean isAgreed(@Param("memberNo") Long memberNo, @Param("termsNo") Long termsNo);

    // 특정 회원이 특정한 약관에 대해 동의를 철회한 상태(activate_yn = 'N')인지 확인한다.
    @Query("SELECT COUNT(th) > 0 FROM TermsHistory th " +
            "WHERE th.member.no = :memberNo " +
            "AND th.terms.no = :termsNo " +
            "AND th.agreeYn = 'N'")
    boolean isRevoked(@Param("memberNo") Long memberNo, @Param("termsNo") Long termsNo);

    // 특정 회원의 활성화된 약관 동의 중 특정 약관 번호로 조회한다.
    @Query("SELECT th FROM TermsHistory th " +
            "WHERE th.member.no = :memberNo " +
            "AND th.activeYn = 'Y' " +
            "AND th.terms.no = :termsNo")
    Optional<TermsHistory> findActiveByMemberAndTerms(@Param("memberNo") Long memberNo, @Param("termsNo") Long termsNo);

    @Query("SELECT th FROM TermsHistory th JOIN FETCH th.terms t WHERE th.member.no = :memberNo AND t.category = :category")
    List<TermsHistory> findAgreedJoinByMember(@Param("memberNo") Long memberNo, @Param("category") TermsCategory category);
}
