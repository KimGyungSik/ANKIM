package shoppingmall.ankim.domain.termsHistory.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.terms.entity.TermsCategory;
import shoppingmall.ankim.domain.terms.repository.TermsRepository;
import shoppingmall.ankim.domain.termsHistory.entity.TermsHistory;
import shoppingmall.ankim.factory.MemberFactory;
import shoppingmall.ankim.factory.TermsHistoryFactory;
import shoppingmall.ankim.global.config.QuerydslConfig;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // 테스트 간 독립성 확보
@Import(QuerydslConfig.class)
class TermsHistoryRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    private TermsHistoryRepository termsHistoryRepository;

    @Autowired
    private TermsRepository termsRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("TermsHistoryFactory를 이용해서 약관 동의내역을 생성한다.")
    void makeTermsHistoryFactory() {
        // given
        List<TermsHistory> termsHistoryList = TermsHistoryFactory.create(em, "test@example.com");

        for (int i = 0; i < termsHistoryList.size(); i++) {
            assertThat(termsHistoryRepository.findById(termsHistoryList.get(i).getNo())).isPresent();
        }
    }


    @Test
    @DisplayName("약관 동의 이력이 있는 경우 회원의 약관동의 내역을 조회할 수 있다.")
    void findByMemberAndTerms_withExistingHistory() {
        // given
        String loginId = "test@example.com";
        List<TermsHistory> termsHistories = TermsHistoryFactory.create(em, "test@example.com");

        Member member = memberRepository.findByLoginId(loginId);
        Terms terms = termsHistories.get(0).getTerms();

        // when
        Optional<TermsHistory> result = termsHistoryRepository.findByMemberAndTerms(member.getNo(), terms.getNo());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getTerms().getName()).isEqualTo(terms.getName());
        assertThat(result.get().getAgreeYn()).isEqualTo("Y");
    }

    @Test
    @DisplayName("약관 동의 이력이 없는 경우 TERMS_NOT_FOUND 에러가 발생한다.")
    void findByMemberAndTerms_withoutHistory() {
        // given
        String loginId = "test@example.com";
        List<TermsHistory> termsHistories = TermsHistoryFactory.create(em, "test@example.com");

        Member member = memberRepository.findByLoginId(loginId);
        Terms terms = termsHistories.get(0).getTerms();

        // when
        Optional<TermsHistory> result = termsHistoryRepository.findByMemberAndTerms(member.getNo(), terms.getNo());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getTerms().getName()).isEqualTo(terms.getName());
        assertThat(result.get().getAgreeYn()).isEqualTo("Y");
    }

}