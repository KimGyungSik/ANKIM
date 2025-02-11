package shoppingmall.ankim.domain.terms.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.terms.dto.TermsAgreeResponse;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.terms.entity.TermsCategory;
import shoppingmall.ankim.domain.terms.repository.TermsRepository;
import shoppingmall.ankim.domain.termsHistory.entity.TermsHistory;
import shoppingmall.ankim.global.config.QuerydslConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
@Import(QuerydslConfig.class) // QuerydslConfig를 테스트에 추가
@Transactional
class TermsServiceTest {

    @Autowired
    private TermsService termsService;

    @Autowired
    private TermsRepository termsRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("사용자의 이전 동의 이력과 최신 약관을 비교하여 최종 동의 여부를 반환한다.")
    void testGetTermsForMember() {
        // Given: 회원 생성
        Member member = Member.builder()
                .loginId("test@example.com")
                .password("password")
                .name("홍길동")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1995, 1, 1))
                .gender("M")
                .status(MemberStatus.ACTIVE)
                .build();
        em.persist(member);

        // Given: 부모 약관 생성
        // 최상위 약관 생성
        Terms mainTerms = Terms.builder()
                .name("회원가입 약관")
                .category(TermsCategory.JOIN)
                .contents("ANKIM 이용약관")
                .termsYn("Y")
                .termsVersion(1)
                .level(1)
                .activeYn("Y")
                .build();
        termsRepository.save(mainTerms);

        Terms subTerm3 = Terms.builder()
                .parentTerms(mainTerms)
                .name("마케팅 목적의 개인정보 수집 및 이용 동의")
                .category(TermsCategory.JOIN)
                .contents("마케팅 목적의 개인정보 수집 및 이용 동의")
                .termsYn("N")
                .termsVersion(1)
                .level(2)
                .activeYn("Y")
                .build();
        termsRepository.save(subTerm3);

        Terms subTerm4 = Terms.builder()
                .parentTerms(mainTerms)
                .name("광고성 정보 수신 동의")
                .category(TermsCategory.JOIN)
                .contents("광고성 정보를 수신하는 것에 대한 동의입니다.")
                .termsYn("N")
                .termsVersion(1)
                .level(2)
                .activeYn("Y")
                .build();
        termsRepository.save(subTerm4);

        // level 3 하위 약관 생성
        Terms subSubTerm1 = Terms.builder()
                .parentTerms(subTerm4)
                .name("문자 수신 동의")
                .category(TermsCategory.JOIN)
                .contents("광고성 정보를 수신하는 것에 대한 동의입니다.")
                .termsYn("N")
                .termsVersion(1)
                .level(3)
                .activeYn("Y")
                .build();
        termsRepository.save(subSubTerm1);

        Terms subSubTerm2 = Terms.builder()
                .parentTerms(subTerm4)
                .name("이메일 수신 동의")
                .category(TermsCategory.JOIN)
                .contents("광고성 정보를 수신하는 것에 대한 동의입니다.")
                .termsYn("N")
                .termsVersion(1)
                .level(3)
                .activeYn("Y")
                .build();
        termsRepository.save(subSubTerm2);

        Terms subTermUpdate4 = Terms.builder()
                .parentTerms(mainTerms)
                .name("광고성 정보 수신 동의")
                .category(TermsCategory.JOIN)
                .contents("약관 변경) 광고성 정보를 수신하는 것에 대한 동의입니다.")
                .termsYn("N")
                .termsVersion(2)
                .level(2)
                .activeYn("N")
                .build();
        termsRepository.save(subTermUpdate4);

        Terms subSubTermUpdate1 = Terms.builder()
                .parentTerms(subTermUpdate4)
                .name("문자 수신 동의")
                .category(TermsCategory.JOIN)
                .contents("약관 변경) 광고성 정보를 수신하는 것에 대한 동의입니다.")
                .termsYn("N")
                .termsVersion(2)
                .level(3)
                .activeYn("N")
                .build();
        termsRepository.save(subSubTermUpdate1);

        Terms subTermV3Update4 = Terms.builder()
                .parentTerms(mainTerms)
                .name("광고성 정보 수신 동의")
                .category(TermsCategory.JOIN)
                .contents("약관 변경) 광고성 정보를 수신하는 것에 대한 동의입니다.")
                .termsYn("N")
                .termsVersion(2)
                .level(2)
                .activeYn("Y")
                .build();
        termsRepository.save(subTermV3Update4);

        Terms subSubTermV3Update1 = Terms.builder()
                .parentTerms(subTermV3Update4)
                .name("문자 수신 동의")
                .category(TermsCategory.JOIN)
                .contents("약관 변경) 광고성 정보를 수신하는 것에 대한 동의입니다.")
                .termsYn("N")
                .termsVersion(3)
                .level(3)
                .activeYn("Y")
                .build();
        termsRepository.save(subSubTermV3Update1);

        Terms subSubTermV3Update2 = Terms.builder()
                .parentTerms(subTermV3Update4)
                .name("이메일 수신 동의")
                .category(TermsCategory.JOIN)
                .contents("약관 변경) 광고성 정보를 수신하는 것에 대한 동의입니다.")
                .termsYn("N")
                .termsVersion(3)
                .level(3)
                .activeYn("Y")
                .build();
        termsRepository.save(subSubTermV3Update2);

        Terms subSubTermV3Update3 = Terms.builder()
                .parentTerms(subTermV3Update4)
                .name("카톡 수신 동의")
                .category(TermsCategory.JOIN)
                .contents("약관 변경) 광고성 정보를 수신하는 것에 대한 동의입니다.")
                .termsYn("N")
                .termsVersion(3)
                .level(3)
                .activeYn("Y")
                .build();
        termsRepository.save(subSubTermV3Update3);

        // 사용자의 동의 내역 추가 (v1 문자 수신 동의)
        TermsHistory agreedSubSubTerm1 = TermsHistory.builder()
                .member(member)
                .terms(subSubTerm1)
                .agreeYn("Y")
                .activeYn("Y")
                .agreeDate(LocalDateTime.now())
                .build();
        em.persist(agreedSubSubTerm1);

        // 사용자의 동의 내역 추가 (마케팅 수신 동의)
        TermsHistory agreedSubTerm3 = TermsHistory.builder()
                .member(member)
                .terms(subTerm3)
                .agreeYn("Y")
                .activeYn("Y")
                .agreeDate(LocalDateTime.now())
                .build();
        em.persist(agreedSubTerm3);

        // 사용자의 동의 내역 추가 (광고성 정보 수신 동의)
        TermsHistory agreedSubTerm4 = TermsHistory.builder()
                .member(member)
                .terms(subTerm4)
                .agreeYn("Y")
                .activeYn("Y")
                .agreeDate(LocalDateTime.now())
                .build();
        em.persist(agreedSubTerm4);

        em.flush();
        em.clear();

        // When: 최신 약관과 기존 동의 이력 비교
        List<TermsAgreeResponse> results = termsService.getTermsForMember(member.getNo(), TermsCategory.JOIN);

        // Then: 검증
        assertThat(results).hasSize(5); // 총 5개의 약관 존재 (기존에 동의한 약관 2개 + 업데이트된 1개 + 새로운 약관 2개)

        // 최신 약관 비교
        for (TermsAgreeResponse response : results) {
            System.out.println("약관명: " + response.getName() + " | 동의 여부: " + response.getAgreeYn());

            if (response.getName().equals("광고성 정보 수신 동의")) {
                assertThat(response.getTermsVersion()).isEqualTo(1); // 기존 버전이 반영됨
                assertThat(response.getAgreeYn()).isEqualTo("Y"); // 기존 약관이므로 동의받은 상태
            }

            if (response.getName().equals("마케팅 목적의 개인정보 수집 및 이용 동의")) {
                assertThat(response.getTermsVersion()).isEqualTo(1); // 기존 버전이 반영됨
                assertThat(response.getAgreeYn()).isEqualTo("Y"); // 기존 약관이므로 동의받은 상태
            }

            if (response.getName().equals("문자 수신 동의")) {
                assertThat(response.getTermsVersion()).isEqualTo(3); // 최신 버전이 반영됨
                assertThat(response.getAgreeYn()).isEqualTo("N"); // 최신 약관으로 다시 동의해야 함
            }

            if (response.getName().equals("이메일 수신 동의")) {
                assertThat(response.getTermsVersion()).isEqualTo(3); // 새로운 약관
                assertThat(response.getAgreeYn()).isEqualTo("N"); // 새 약관이므로 기본 N
            }

            if (response.getName().equals("카톡 수신 동의")) {
                assertThat(response.getTermsVersion()).isEqualTo(3); // 새로운 약관
                assertThat(response.getAgreeYn()).isEqualTo("N"); // 새 약관이므로 기본 N
            }
        }
    }
}