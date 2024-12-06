package shoppingmall.ankim.domain.termsHistory.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.terms.entity.TermsCategory;
import shoppingmall.ankim.domain.terms.repository.TermsRepository;
import shoppingmall.ankim.domain.termsHistory.entity.TermsHistory;
import shoppingmall.ankim.factory.MemberFactory;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
class TermsHistoryRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    private TermsHistoryRepository termsHistoryRepository;

    @Autowired
    private TermsRepository termsRepository;

//    @Test
//    @DisplayName("약관 동의 이력 조회 - 동의 이력이 있는 경우")
//    void findByMemberAndTerms_withExistingHistory() {
//        // given
//        Member member = MemberFactory.createMember(em, "test@example.com");
//
//        Terms terms = termsRepository.save(
//                Terms.builder()
//                        .name("서비스 이용 약관")
//                        .category(TermsCategory.JOIN)
//                        .level(3)
//                        .termsYn("Y")
//                        .termsVersion("v1")
//                        .activeYn("Y")
//                        .build()
//        );

//        TermsHistory termsHistory = termsHistoryRepository.save(
//                TermsHistory.builder()
//                        .member(member)
//                        .terms(terms)
//                        .agreeYn("Y")
//                        .agreeDate(LocalDateTime.now())
//                        .activeYn("Y")
//                        .build()
//        );

        // when
//        Optional<TermsHistory> result = termsHistoryRepository.findByMemberAndTerms(member.getNo(), terms.getNo());

        // then
//        assertThat(result).isPresent();
//        assertThat(result.get().getTerms().getName()).isEqualTo("서비스 이용 약관");
//        assertThat(result.get().getAgreeYn()).isEqualTo("Y");
//    }
}