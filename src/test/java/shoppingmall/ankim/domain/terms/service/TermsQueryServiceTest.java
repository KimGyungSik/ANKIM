package shoppingmall.ankim.domain.terms.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.terms.dto.TermsJoinResponse;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.terms.entity.TermsCategory;
import shoppingmall.ankim.domain.terms.exception.TermsMandatoryNotAgreeException;
import shoppingmall.ankim.domain.terms.repository.TermsRepository;
import shoppingmall.ankim.domain.terms.service.query.TermsQueryService;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsAgreement;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@TestPropertySource(properties = "spring.sql.init.mode=never")
class TermsQueryServiceTest {

    @Autowired
    private TermsQueryService termsQueryService;

    @Autowired
    private TermsRepository termsRepository;

    @MockBean
    private S3Service s3Service;

    // validateTerms 테스트
    @Test
    @DisplayName("필수약관을 동의하지 않은 경우 지정한 예외가 발생한다.")
    void notAgreedMandatoryTerms() {
        // given
        List<TermsAgreement> termsAgreements = new ArrayList<>();
        termsAgreements.add(TermsAgreement.builder()
                .no(1L)
                .name("필수 약관")
                .termsYn("Y")
                .agreeYn("N") // 필수 약관에 동의하지 않음
                .level(2)
                .build()
        );

        // when, then
        assertThrows(TermsMandatoryNotAgreeException.class, () ->
                termsQueryService.validateAndAddSubTerms(termsAgreements)
        );
    }

    @Test
    @DisplayName("회원가입 시 레벨2의 약관을 가져오고, TermsJoinResponse에 올바르게 변환된다.")
    void findJoinTerm() {
        // given
        TermsCategory category = TermsCategory.JOIN;
        String activeYn = "Y";

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

        // when
        List<TermsJoinResponse> joinTermsResponses = termsQueryService.findJoinTerm();

        // then
        assertThat(joinTermsResponses)
                .hasSize(2)
                .extracting("name", "termsYn", "level")
                .containsExactlyInAnyOrder(
                        tuple("만 14세 이상", "Y", 2),
                        tuple("광고 수신 동의", "Y", 2)
                );
    }


    @Test
    @DisplayName("광고 수신 동의에 동의 시 하위 약관인 문자 수신 동의도 리스트에 포함되는지 확인한다.")
    void validateAndAddSubTerms_WithSubTerms() {
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
                .activeYn(activeYn)
                .build();
        termsRepository.save(mainTerms);

        // level 2 약관 생성
        Terms subTerm1 = Terms.builder()
                .parentTerms(mainTerms)
                .name("만 14세 이상")
                .category(category)
                .contents("만 14세 이상")
                .termsYn("Y")
                .termsVersion("v1")
                .level(2)
                .activeYn(activeYn)
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
                .activeYn(activeYn)
                .build();
        termsRepository.save(subTerm2);

        // level 3 약관 생성 (광고 수신 동의의 하위 약관)
        Terms subSubTerm1 = Terms.builder()
                .parentTerms(subTerm2)
                .name("문자 수신 동의")
                .category(category)
                .contents("광고성 문자 수신 동의")
                .termsYn("Y")
                .termsVersion("v1")
                .level(3)
                .activeYn(activeYn)
                .build();
        termsRepository.save(subSubTerm1);

        // 동의 요청 생성
        List<TermsAgreement> termsAgreements = new ArrayList<>();
        termsAgreements.add(TermsAgreement.builder()
                .no(subTerm1.getNo())  // 14세 이상 동의
                .name("만 14세 이상")
                .termsYn("Y")
                .agreeYn("Y") // 약관 동의
                .level(2)
                .build()
        );
        termsAgreements.add(TermsAgreement.builder()
                .no(subTerm2.getNo())  // 광고 수신 동의
                .name("광고 수신 동의")
                .termsYn("N")
                .agreeYn("Y") // 광고 수신 동의
                .level(2)
                .build()
        );

        // when
        List<Terms> result = termsQueryService.validateAndAddSubTerms(termsAgreements);

        // then
        assertThat(result)
                .hasSize(3)
                .extracting("name")
                .containsExactlyInAnyOrder(
                        "만 14세 이상", "광고 수신 동의", "문자 수신 동의"
                );
    }

    @Test
    @DisplayName("필수 약관을 동의하지 않은 경우 사용자 정의 예외가 발생한다.")
    void notAgreedMandatoryTermsWithMissingRequiredAgreement() {
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
                .activeYn(activeYn)
                .build();
        termsRepository.save(mainTerms);

        // level 2 약관 생성
        Terms subTerm1 = Terms.builder()
                .parentTerms(mainTerms)
                .name("만 14세 이상")
                .category(category)
                .contents("만 14세 이상")
                .termsYn("Y")
                .termsVersion("v1")
                .level(2)
                .activeYn(activeYn)
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
                .activeYn(activeYn)
                .build();
        termsRepository.save(subTerm2);

        // level 3 약관 생성 (광고 수신 동의의 하위 약관)
        Terms subSubTerm1 = Terms.builder()
                .parentTerms(subTerm2)
                .name("문자 수신 동의")
                .category(category)
                .contents("광고성 문자 수신 동의")
                .termsYn("Y")
                .termsVersion("v1")
                .level(3)
                .activeYn(activeYn)
                .build();
        termsRepository.save(subSubTerm1);

        // 동의 요청 생성
        List<TermsAgreement> termsAgreements = new ArrayList<>();
        termsAgreements.add(TermsAgreement.builder()
                .no(subTerm1.getNo())  // 14세 이상 동의
                .name("만 14세 이상")
                .termsYn("Y")
                .agreeYn("N") // 약관 동의
                .level(2)
                .build()
        );
        termsAgreements.add(TermsAgreement.builder()
                .no(subTerm2.getNo())  // 광고 수신 동의
                .name("광고 수신 동의")
                .termsYn("N")
                .agreeYn("Y") // 광고 수신 동의
                .level(2)
                .build()
        );

        // when, then
        assertThrows(TermsMandatoryNotAgreeException.class, () ->
                termsQueryService.validateAndAddSubTerms(termsAgreements)
        );
    }

}