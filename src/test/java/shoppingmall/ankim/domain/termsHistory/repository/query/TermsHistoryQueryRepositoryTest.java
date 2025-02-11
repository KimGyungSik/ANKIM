package shoppingmall.ankim.domain.termsHistory.repository.query;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.member.dto.TermsAgreementResponse;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.terms.entity.TermsCategory;
import shoppingmall.ankim.domain.terms.repository.TermsRepository;
import shoppingmall.ankim.domain.terms.repository.query.TermsQueryRepository;
import shoppingmall.ankim.domain.termsHistory.entity.TermsHistory;
import shoppingmall.ankim.domain.termsHistory.repository.TermsHistoryRepository;
import shoppingmall.ankim.global.config.QuerydslConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
@Import(QuerydslConfig.class) // QuerydslConfig를 테스트에 추가
class TermsHistoryQueryRepositoryTest {

    @Autowired
    private TermsHistoryRepository termsHistoryRepository;
    @Autowired
    private TermsRepository termsRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("특정 부모약관을 기준으로 하위 약관을 포함한 약관 조회 및 사용자의 동의 내역을 조회한다.")
    void testFindAgreedTermsByMember() {
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
        entityManager.persist(member);

        // Given: 부모 약관 생성
        // 최상위 약관 생성
        Terms mainTerms = Terms.builder()
                .name("회원가입 약관")
                .category(TermsCategory.JOIN)
                .contents("ANKIM 이용약관")
                .termsYn("Y")
//                .termsVersion("v1")
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
//                .termsVersion("v1")
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
//                .termsVersion("v1")
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
//                .termsVersion("v1")
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
//                .termsVersion("v1")
                .termsVersion(1)
                .level(3)
                .activeYn("Y")
                .build();
        termsRepository.save(subSubTerm2);

        // Given: 사용자의 동의 내역 추가 (문자 수신 동의만 동의)
        TermsHistory agreedSubSubTerm1 = TermsHistory.builder()
                .member(member)
                .terms(subSubTerm1)
                .agreeYn("Y")
                .activeYn("Y")
                .agreeDate(LocalDateTime.now())
                .build();
        entityManager.persist(agreedSubSubTerm1);

//        TermsHistory agreedSubSubTerm2 = TermsHistory.builder()
//                .member(member)
//                .terms(subSubTerm2)
//                .agreeYn("Y")
//                .activeYn("Y")
//                .agreeDate(LocalDateTime.now())
//                .build();
//        entityManager.persist(agreedSubSubTerm2);

        entityManager.flush();
        entityManager.clear();

        // When: 특정 parentNo를 기준으로 하위 약관을 포함한 약관 조회 및 사용자의 동의 내역 조회
        List<TermsAgreementResponse> results1 = termsHistoryRepository.findAgreedTermsByMember(member.getNo(), subTerm4.getNo(), "Y");
        List<TermsAgreementResponse> results2 = termsHistoryRepository.findAgreedTermsByMember(member.getNo(), subTerm3.getNo(), "Y");

        List<TermsAgreementResponse> results = new ArrayList<>();
        results.addAll(results1);
        results.addAll(results2);

        // Then: 조회 결과 검증
        assertThat(results).isNotEmpty();
        assertThat(results).hasSize(4); // 부모 약관 + 하위 약관 2개

        for (TermsAgreementResponse result : results) {
            System.out.print("result.getName() = " + result.getName());
            System.out.println("| result.getAgreeYn() = " + result.getAgreeYn());
        }
        
        // 부모 약관
        assertThat(results.get(0).getTermsNo()).isEqualTo(subTerm4.getNo());
        assertThat(results.get(0).getName()).isEqualTo(subTerm4.getName());
        assertThat(results.get(0).getAgreeYn()).isNull(); // 동의한 적 없음

        // 하위 약관 1 (동의 O)
        assertThat(results.get(1).getTermsNo()).isEqualTo(subSubTerm1.getNo());
        assertThat(results.get(1).getName()).isEqualTo(subSubTerm1.getName());
        assertThat(results.get(1).getAgreeYn()).isEqualTo("Y");

        // 하위 약관 2 (동의 X)
        assertThat(results.get(2).getTermsNo()).isEqualTo(subSubTerm2.getNo());
        assertThat(results.get(2).getName()).isEqualTo(subSubTerm2.getName());
        assertThat(results.get(2).getAgreeYn()).isNull(); // 동의한 적 없음

        // 부모 약관
        assertThat(results.get(3).getTermsNo()).isEqualTo(subTerm3.getNo());
        assertThat(results.get(3).getName()).isEqualTo(subTerm3.getName());
        assertThat(results.get(3).getAgreeYn()).isNull(); // 동의한 적 없음
    }

    @Test
    @DisplayName("특정 약관이 v3까지 업데이트 되었을 때에도 기존의 사용자의 동의 내역을 조회할 수 있다.")
    void testFindAgreedTermsUpdateByMember() {
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
        entityManager.persist(member);

        // Given: 부모 약관 생성
        // 최상위 약관 생성
        Terms mainTerms = Terms.builder()
                .name("회원가입 약관")
                .category(TermsCategory.JOIN)
                .contents("ANKIM 이용약관")
                .termsYn("Y")
//                .termsVersion("v1")
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
//                .termsVersion("v1")
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
//                .termsVersion("v1")
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
//                .termsVersion("v1")
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
//                .termsVersion("v1")
                .termsVersion(1)
                .level(3)
                .activeYn("Y")
                .build();
        termsRepository.save(subSubTerm2);

        Terms subTermUpdate4 = Terms.builder()
                .parentTerms(mainTerms)
                .name("약관 변경 v2) 광고성 정보 수신 동의")
                .category(TermsCategory.JOIN)
                .contents("약관 변경) 광고성 정보를 수신하는 것에 대한 동의입니다.")
                .termsYn("N")
//                .termsVersion("v2")
                .termsVersion(2)
                .level(2)
                .activeYn("N")
                .build();
        termsRepository.save(subTermUpdate4);

        Terms subSubTermUpdate1 = Terms.builder()
                .parentTerms(subTermUpdate4)
                .name("약관 변경 v2) 문자 수신 동의")
                .category(TermsCategory.JOIN)
                .contents("약관 변경) 광고성 정보를 수신하는 것에 대한 동의입니다.")
                .termsYn("N")
//                .termsVersion("v2")
                .termsVersion(2)
                .level(3)
                .activeYn("N")
                .build();
        termsRepository.save(subSubTermUpdate1);

        Terms subTermV3Update4 = Terms.builder()
                .parentTerms(mainTerms)
                .name("약관 변경 v3) 광고성 정보 수신 동의")
                .category(TermsCategory.JOIN)
                .contents("약관 변경) 광고성 정보를 수신하는 것에 대한 동의입니다.")
                .termsYn("N")
//                .termsVersion("v2")
                .termsVersion(2)
                .level(2)
                .activeYn("Y")
                .build();
        termsRepository.save(subTermV3Update4);

        Terms subSubTermV3Update1 = Terms.builder()
                .parentTerms(subTermV3Update4)
                .name("약관 변경 v3) 문자 수신 동의")
                .category(TermsCategory.JOIN)
                .contents("약관 변경) 광고성 정보를 수신하는 것에 대한 동의입니다.")
                .termsYn("N")
//                .termsVersion("v3")
                .termsVersion(3)
                .level(3)
                .activeYn("Y")
                .build();
        termsRepository.save(subSubTermV3Update1);

        Terms subSubTermV3Update2 = Terms.builder()
                .parentTerms(subTermV3Update4)
                .name("약관 변경 v3) 이메일 수신 동의")
                .category(TermsCategory.JOIN)
                .contents("약관 변경) 광고성 정보를 수신하는 것에 대한 동의입니다.")
                .termsYn("N")
//                .termsVersion("v3")
                .termsVersion(3)
                .level(3)
                .activeYn("Y")
                .build();
        termsRepository.save(subSubTermV3Update2);

        Terms subSubTermV3Update3 = Terms.builder()
                .parentTerms(subTermV3Update4)
                .name("약관 변경 v3) 카톡 수신 동의")
                .category(TermsCategory.JOIN)
                .contents("약관 변경) 광고성 정보를 수신하는 것에 대한 동의입니다.")
                .termsYn("N")
//                .termsVersion("v3")
                .termsVersion(3)
                .level(3)
                .activeYn("Y")
                .build();
        termsRepository.save(subSubTermV3Update3);

        // Given: 사용자의 동의 내역 추가 (문자 수신 동의만 동의)
        TermsHistory agreedSubSubTerm1 = TermsHistory.builder()
                .member(member)
                .terms(subSubTerm1)
                .agreeYn("Y")
                .activeYn("Y")
                .agreeDate(LocalDateTime.now())
                .build();
        entityManager.persist(agreedSubSubTerm1);

        entityManager.flush();
        entityManager.clear();

        List<Terms> termsList = termsRepository.findAll();
        System.out.println("저장된 약관 개수: " + termsList.size());
        termsList.forEach(t -> System.out.println(t.getNo() + " | " + t.getName() + " | " + t.getTermsVersion()));

        // When: 특정 parentNo를 기준으로 하위 약관을 포함한 약관 조회 및 사용자의 동의 내역 조회
        List<TermsAgreementResponse> results1 = termsHistoryRepository.findAgreedTermsByMember(member.getNo(), subTerm4.getNo(), "Y");
        List<TermsAgreementResponse> results2 = termsHistoryRepository.findAgreedTermsByMember(member.getNo(), subTerm3.getNo(), "Y");

        List<TermsAgreementResponse> results = new ArrayList<>();
        results.addAll(results1);
        results.addAll(results2);

        for (TermsAgreementResponse result : results) {
            System.out.println("약관명: " + result.getName() + " | 동의 여부: " + result.getAgreeYn());
        }

        // Then: 조회 결과 검증
        assertThat(results).isNotEmpty();
        assertThat(results).hasSize(4); // 부모 약관 + 하위 약관 2개

        for (TermsAgreementResponse result : results) {
            System.out.print("result.getName() = " + result.getName());
            System.out.println("| result.getAgreeYn() = " + result.getAgreeYn());
        }

        // 부모 약관
        assertThat(results.get(0).getTermsNo()).isEqualTo(subTerm4.getNo());
        assertThat(results.get(0).getName()).isEqualTo(subTerm4.getName());
        assertThat(results.get(0).getAgreeYn()).isNull(); // 동의한 적 없음

        // 하위 약관 1 (동의 O)
        assertThat(results.get(1).getTermsNo()).isEqualTo(subSubTerm1.getNo());
        assertThat(results.get(1).getName()).isEqualTo(subSubTerm1.getName());
        assertThat(results.get(1).getAgreeYn()).isEqualTo("Y");

        // 하위 약관 2 (동의 X)
        assertThat(results.get(2).getTermsNo()).isEqualTo(subSubTerm2.getNo());
        assertThat(results.get(2).getName()).isEqualTo(subSubTerm2.getName());
        assertThat(results.get(2).getAgreeYn()).isNull(); // 동의한 적 없음

        // 부모 약관
        assertThat(results.get(3).getTermsNo()).isEqualTo(subTerm3.getNo());
        assertThat(results.get(3).getName()).isEqualTo(subTerm3.getName());
        assertThat(results.get(3).getAgreeYn()).isNull(); // 동의한 적 없음
    }
}