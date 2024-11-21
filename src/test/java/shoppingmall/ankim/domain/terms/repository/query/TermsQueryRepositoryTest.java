package shoppingmall.ankim.domain.terms.repository.query;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import shoppingmall.ankim.domain.security.repository.TokenRepository;
import shoppingmall.ankim.domain.terms.dto.TermsJoinResponse;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.terms.entity.TermsCategory;
import shoppingmall.ankim.domain.terms.repository.TermsRepository;
import shoppingmall.ankim.global.config.QuerydslConfig;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
@Import(QuerydslConfig.class) // QuerydslConfig를 테스트에 추가
class TermsQueryRepositoryTest {

    @Autowired
    private TermsRepository termsRepository;
    @Autowired
    private EntityManager em;
    @MockBean
    private TokenRepository tokenRepository;

    @Test
    @DisplayName("최상위 약관을 기준으로 모든 하위 약관을 재귀적으로 조회한다.")
    void findAllSubTermsRecursively() {
        // given
        TermsCategory category = TermsCategory.JOIN;
        String activeYn = "Y";

        // 최상위 약관 생성
        Terms mainTerms = Terms.builder()
                .name("회원가입 약관")
                .category(category)
                .contents("ANKIM 회원가입 약관")
                .termsYn("N")
                .termsVersion("v1")
                .level(1)
                .activeYn("Y")
                .build();
        termsRepository.save(mainTerms);

        // 하위 약관 생성
        Terms subTerm1 = Terms.builder()
                .parentTerms(mainTerms)
                .name("만 14세 이상")
                .category(category)
                .contents("만 14세 이상")
                .termsYn("Y")
                .termsVersion("v1")
                .level(2)
                .activeYn("Y")
                .build();
        termsRepository.save(subTerm1);

        Terms subTerm2 = Terms.builder()
                .parentTerms(mainTerms)
                .name("광고 수신 동의")
                .category(category)
                .contents("광고성 연락 수신 동의")
                .termsYn("Y")
                .termsVersion("v1")
                .level(2)
                .activeYn("Y")
                .build();
        termsRepository.save(subTerm2);

        Terms subSubTerm1 = Terms.builder()
                .parentTerms(subTerm2)
                .name("문자 수신 동의")
                .category(category)
                .contents("광고성 문자 수신 동의")
                .termsYn("Y")
                .termsVersion("v1")
                .level(3)
                .activeYn("Y")
                .build();
        termsRepository.save(subSubTerm1);

        // 최상위 약관 생성
        Terms mainTerms2 = Terms.builder()
                .name("주문결제 약관")
                .category(TermsCategory.ORDER)
                .contents("ANKIM 주문결제 약관")
                .termsYn("N")
                .termsVersion("v1")
                .level(1)
                .activeYn("Y")
                .build();
        termsRepository.save(mainTerms);

        em.flush();
        em.clear();

        // when
        List<Terms> allTerms = termsRepository.findAllSubTermsRecursively(category, activeYn);

        for (Terms terms : allTerms) {
            System.out.println("terms.getName() = " + terms.getName());
        }
        
        // then
        assertNotNull(allTerms);
        assertEquals(4, allTerms.size()); // 총 4개의 약관이 조회되어야 함
        assertEquals("회원가입 약관", allTerms.get(0).getName());
        assertEquals("만 14세 이상", allTerms.get(1).getName());
        assertEquals("광고 수신 동의", allTerms.get(2).getName());
        assertEquals("문자 수신 동의", allTerms.get(3).getName());
    }

    @Test
    @DisplayName("특정 레벨의 하위 약관을 조회한다.")
    void findLevelSubTerms() {
        // given
        TermsCategory category = TermsCategory.JOIN;
        String activeYn = "Y";
        Integer level = 2;

        // 최상위 약관 생성
        Terms mainTerms = Terms.builder()
                .name("회원가입 약관")
                .category(category)
                .contents("ANKIM 이용약관")
                .termsYn("Y")
                .termsVersion("v1")
                .level(1)
                .activeYn("Y")
                .build();
        termsRepository.save(mainTerms);

        // level 2 필수 약관 생성
        Terms subTerm1 = Terms.builder()
                .parentTerms(mainTerms)
                .name("만 14세 이상입니다")
                .category(category)
                .contents("이 약관은 만 14세 이상임을 동의하는 내용입니다.")
                .termsYn("Y")
                .termsVersion("v1")
                .level(2)
                .activeYn("Y")
                .build();
        termsRepository.save(subTerm1);

        Terms subTerm2 = Terms.builder()
                .parentTerms(mainTerms)
                .name("이용약관 동의")
                .category(category)
                .contents("이 약관은 서비스 이용에 대한 동의를 포함합니다.")
                .termsYn("Y")
                .termsVersion("v1")
                .level(2)
                .activeYn("Y")
                .build();
        termsRepository.save(subTerm2);

        // level 2 선택 약관 생성
        Terms subTerm3 = Terms.builder()
                .parentTerms(mainTerms)
                .name("마케팅 목적의 개인정보 수집 및 이용 동의")
                .category(category)
                .contents("마케팅 목적으로 개인정보를 수집 및 이용하는 것에 대한 동의입니다.")
                .termsYn("N")
                .termsVersion("v1")
                .level(2)
                .activeYn("Y")
                .build();
        termsRepository.save(subTerm3);

        Terms subTerm4 = Terms.builder()
                .parentTerms(mainTerms)
                .name("광고성 정보 수신 동의")
                .category(category)
                .contents("광고성 정보를 수신하는 것에 대한 동의입니다.")
                .termsYn("N")
                .termsVersion("v1")
                .level(2)
                .activeYn("Y")
                .build();
        termsRepository.save(subTerm4);

        // level 3 하위 약관 생성
        Terms subSubTerm1 = Terms.builder()
                .parentTerms(subTerm4)
                .name("문자 수신 동의")
                .category(category)
                .contents("광고성 정보를 수신하는 것에 대한 동의입니다.")
                .termsYn("N")
                .termsVersion("v1")
                .level(3)
                .activeYn("Y")
                .build();
        termsRepository.save(subSubTerm1);

        Terms subSubTerm2 = Terms.builder()
                .parentTerms(subTerm4)
                .name("이메일 수신 동의")
                .category(category)
                .contents("광고성 정보를 수신하는 것에 대한 동의입니다.")
                .termsYn("N")
                .termsVersion("v1")
                .level(3)
                .activeYn("Y")
                .build();
        termsRepository.save(subSubTerm2);

        em.flush();
        em.clear();

        // when
        List<TermsJoinResponse> level2Terms = termsRepository.findLevelSubTerms(category, level, activeYn);
        
        for(TermsJoinResponse level2Term : level2Terms) {
            System.out.println("level2Term.getName() = " + level2Term.getName());
        }

        // then
        assertNotNull(level2Terms);
        assertEquals(4, level2Terms.size()); // level 2 약관이 4개여야 함
        assertEquals("만 14세 이상입니다", level2Terms.get(0).getName());
        assertEquals("이용약관 동의", level2Terms.get(1).getName());
        assertEquals("마케팅 목적의 개인정보 수집 및 이용 동의", level2Terms.get(2).getName());
        assertEquals("광고성 정보 수신 동의", level2Terms.get(3).getName());
    }

    @Test
    @DisplayName("회원가입 시 광고 수신을 동의하는 경우 하위 레벨의 약관을 가져올 수 있다.")
    void findSubTermsForParent() {
        // given
        TermsCategory category = TermsCategory.JOIN;
        String active = "Y";

        // 최상위 약관 생성
        Terms mainTerms = Terms.builder()
                .name("회원가입 약관")
                .category(category)
                .contents("ANKIM 회원가입 약관")
                .termsYn("N")
                .termsVersion("v1")
                .level(1)
                .activeYn("Y")
                .build();
        termsRepository.save(mainTerms);

        // 하위 약관 생성
        Terms subTerm1 = Terms.builder()
                .parentTerms(mainTerms)
                .name("나이 약관")
                .category(category)
                .contents("나이 약관")
                .termsYn("Y")
                .termsVersion("v1")
                .level(2)
                .activeYn("Y")
                .build();
        termsRepository.save(subTerm1);

        Terms sub1SubTerm1 = Terms.builder()
                .parentTerms(mainTerms)
                .name("만 14세 이상")
                .category(category)
                .contents("만 14세 이상")
                .termsYn("Y")
                .termsVersion("v1")
                .level(2)
                .activeYn("Y")
                .build();
        termsRepository.save(sub1SubTerm1);

        Terms subTerm2 = Terms.builder()
                .parentTerms(mainTerms)
                .name("광고 수신 동의")
                .category(category)
                .contents("광고성 연락 수신 동의")
                .termsYn("Y")
                .termsVersion("v1")
                .level(2)
                .activeYn("Y")
                .build();
        termsRepository.save(subTerm2);

        Terms sub2SubTerm1 = Terms.builder()
                .parentTerms(subTerm2)
                .name("문자 수신 동의")
                .category(category)
                .contents("광고성 문자 수신 동의")
                .termsYn("Y")
                .termsVersion("v1")
                .level(3)
                .activeYn("Y")
                .build();
        termsRepository.save(sub2SubTerm1);

        Terms sub2SubTerm2 = Terms.builder()
                .parentTerms(subTerm2)
                .name("이메일 수신 동의")
                .category(category)
                .contents("광고성 이메일 수신 동의")
                .termsYn("Y")
                .termsVersion("v1")
                .level(3)
                .activeYn("Y")
                .build();
        termsRepository.save(sub2SubTerm2);

        // 최상위 약관 생성
        Terms mainTerms2 = Terms.builder()
                .name("주문결제 약관")
                .category(TermsCategory.ORDER)
                .contents("ANKIM 주문결제 약관")
                .termsYn("N")
                .termsVersion("v1")
                .level(1)
                .activeYn("Y")
                .build();
        termsRepository.save(mainTerms);

        em.flush();
        em.clear();

        // when
        List<Terms> subTerms = termsRepository.findSubTermsForParent(subTerm2.getNo(), (subTerm2.getLevel()+1), active);

        // then
        assertNotNull(subTerms);
        assertThat(subTerms).hasSize(2)
                .extracting("no", "level", "name")
                .containsExactlyInAnyOrder(
                        tuple(sub2SubTerm1.getNo(), 3, "문자 수신 동의"),
                        tuple(sub2SubTerm2.getNo(), 3, "이메일 수신 동의")
                );
    }

    @Test
    @DisplayName("특정 약관과 그 하위 약관을 포함하여 조회한다.")
    void findTermsWithSubTermsIncludingParent() {
        // given
        TermsCategory category = TermsCategory.JOIN;
        String active = "Y";

        // 최상위 약관 생성
        Terms mainTerms = Terms.builder()
                .name("회원가입 약관")
                .category(category)
                .contents("ANKIM 회원가입 약관")
                .termsYn("N")
                .termsVersion("v1")
                .level(1)
                .activeYn("Y")
                .build();
        termsRepository.save(mainTerms);

        // 하위 약관 생성
        Terms subTerm1 = Terms.builder()
                .parentTerms(mainTerms)
                .name("나이 약관")
                .category(category)
                .contents("나이 약관")
                .termsYn("Y")
                .termsVersion("v1")
                .level(2)
                .activeYn("Y")
                .build();
        termsRepository.save(subTerm1);

        Terms sub1SubTerm1 = Terms.builder()
                .parentTerms(mainTerms)
                .name("만 14세 이상")
                .category(category)
                .contents("만 14세 이상")
                .termsYn("Y")
                .termsVersion("v1")
                .level(2)
                .activeYn("Y")
                .build();
        termsRepository.save(sub1SubTerm1);

        Terms subTerm2 = Terms.builder()
                .parentTerms(mainTerms)
                .name("광고 수신 동의")
                .category(category)
                .contents("광고성 연락 수신 동의")
                .termsYn("Y")
                .termsVersion("v1")
                .level(2)
                .activeYn("Y")
                .build();
        termsRepository.save(subTerm2);

        Terms sub2SubTerm1 = Terms.builder()
                .parentTerms(subTerm2)
                .name("문자 수신 동의")
                .category(category)
                .contents("광고성 문자 수신 동의")
                .termsYn("Y")
                .termsVersion("v1")
                .level(3)
                .activeYn("Y")
                .build();
        termsRepository.save(sub2SubTerm1);

        Terms sub2SubTerm2 = Terms.builder()
                .parentTerms(subTerm2)
                .name("이메일 수신 동의")
                .category(category)
                .contents("광고성 이메일 수신 동의")
                .termsYn("Y")
                .termsVersion("v1")
                .level(3)
                .activeYn("Y")
                .build();
        termsRepository.save(sub2SubTerm2);

        // 최상위 약관 생성
        Terms mainTerms2 = Terms.builder()
                .name("주문결제 약관")
                .category(TermsCategory.ORDER)
                .contents("ANKIM 주문결제 약관")
                .termsYn("N")
                .termsVersion("v1")
                .level(1)
                .activeYn("Y")
                .build();
        termsRepository.save(mainTerms);

        em.flush();
        em.clear();

        // when
        List<Terms> result = termsRepository.findSubTermsIncludingParent(subTerm2.getNo(), 2, active);

        // then
        assertNotNull(result);
        assertThat(result).hasSize(3)
                .extracting("no", "level", "name")
                .containsExactlyInAnyOrder(
                        tuple(subTerm2.getNo(), 2, "광고 수신 동의"),
                        tuple(sub2SubTerm1.getNo(), 3, "문자 수신 동의"),
                        tuple(sub2SubTerm2.getNo(), 3, "이메일 수신 동의")
                );
    }

    @Test
    @DisplayName("특정 약관에 하위약관이 없는 경우 특정 약관만 조회한다.")
    void findTermsWithNotSubTermsIncludingParent() {
        // given
        TermsCategory category = TermsCategory.JOIN;
        String active = "Y";

        // 최상위 약관 생성
        Terms mainTerms = Terms.builder()
                .name("회원가입 약관")
                .category(category)
                .contents("ANKIM 회원가입 약관")
                .termsYn("N")
                .termsVersion("v1")
                .level(1)
                .activeYn("Y")
                .build();
        termsRepository.save(mainTerms);

        // 하위 약관 생성
        Terms subTerm1 = Terms.builder()
                .parentTerms(mainTerms)
                .name("나이 약관")
                .category(category)
                .contents("나이 약관")
                .termsYn("Y")
                .termsVersion("v1")
                .level(2)
                .activeYn("Y")
                .build();
        termsRepository.save(subTerm1);

        Terms sub1SubTerm1 = Terms.builder()
                .parentTerms(mainTerms)
                .name("만 14세 이상")
                .category(category)
                .contents("만 14세 이상")
                .termsYn("Y")
                .termsVersion("v1")
                .level(2)
                .activeYn("Y")
                .build();
        termsRepository.save(sub1SubTerm1);

        Terms subTerm2 = Terms.builder()
                .parentTerms(mainTerms)
                .name("광고 수신 동의")
                .category(category)
                .contents("광고성 연락 수신 동의")
                .termsYn("Y")
                .termsVersion("v1")
                .level(2)
                .activeYn("Y")
                .build();
        termsRepository.save(subTerm2);

        Terms sub2SubTerm1 = Terms.builder()
                .parentTerms(subTerm2)
                .name("문자 수신 동의")
                .category(category)
                .contents("광고성 문자 수신 동의")
                .termsYn("Y")
                .termsVersion("v1")
                .level(3)
                .activeYn("Y")
                .build();
        termsRepository.save(sub2SubTerm1);

        Terms sub2SubTerm2 = Terms.builder()
                .parentTerms(subTerm2)
                .name("이메일 수신 동의")
                .category(category)
                .contents("광고성 이메일 수신 동의")
                .termsYn("Y")
                .termsVersion("v1")
                .level(3)
                .activeYn("Y")
                .build();
        termsRepository.save(sub2SubTerm2);

        // 최상위 약관 생성
        Terms mainTerms2 = Terms.builder()
                .name("주문결제 약관")
                .category(TermsCategory.ORDER)
                .contents("ANKIM 주문결제 약관")
                .termsYn("N")
                .termsVersion("v1")
                .level(1)
                .activeYn("Y")
                .build();
        termsRepository.save(mainTerms);

        em.flush();
        em.clear();

        // when
        List<Terms> result = termsRepository.findSubTermsIncludingParent(sub1SubTerm1.getNo(), 2, active);

        // then
        assertNotNull(result);
        assertThat(result).hasSize(1)
                .extracting("no", "level", "name")
                .containsExactlyInAnyOrder(
                        tuple(sub1SubTerm1.getNo(), 2, "만 14세 이상")
                );
    }

}