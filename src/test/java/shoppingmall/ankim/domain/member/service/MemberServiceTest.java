package shoppingmall.ankim.domain.member.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.member.dto.MemberResponse;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.member.service.request.MemberRegisterServiceRequest;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.terms.entity.TermsCategory;
import shoppingmall.ankim.domain.terms.repository.TermsRepository;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsAgreement;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    MemberRepository memberRepository;  // 실제 DB 테스트에 사용되는 memberRepository

    @Autowired
    private TermsRepository termsRepository;

    @Test
    @DisplayName("회원가입 정보가 Member 테이블에 정상적으로 저장된다.")
    void registerMemberTest() {
        // given
        MemberRegisterServiceRequest request = MemberRegisterServiceRequest.builder()
                .loginId("test@example.com")
                .pwd("ValidPassword123!")
                .name("홍길동")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .gender("M")
                .build();
        List<TermsAgreement> termsAgreements = new ArrayList<>();

        // when
        memberService.registerMember(request, termsAgreements);

        // then
        Member savedMember = memberRepository.findByLoginId(request.getLoginId());

        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getName()).isEqualTo("홍길동");
        assertThat(savedMember.getPhoneNum()).isEqualTo("010-1234-5678");
    }

    @Test
    @DisplayName("약관동의 및 회원가입 정보가 Member 테이블에 정상적으로 저장된다.")
    void registerMemberAndTermsTest() {
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

        // 약관 동의
        List<TermsAgreement> termsAgreements = new ArrayList<>();
        termsAgreements.add(TermsAgreement.builder()
                .no(3L)  // 약관 번호 예시
                .name("만 14세 이상")
                .termsYn("Y")  // 필수 동의
                .agreeYn("Y")  // 동의 여부
                .level(2)
                .build());
        termsAgreements.add(TermsAgreement.builder()
                .no(4L)  // 약관 번호 예시
                .name("광고 수신 동의")
                .termsYn("N")  // 필수 동의
                .agreeYn("Y")  // 동의 여부
                .level(2)
                .build());

        // 회원 정보
        MemberRegisterServiceRequest request = MemberRegisterServiceRequest.builder()
                .loginId("test@example.com")
                .pwd("ValidPassword123!")
                .name("홍길동")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .gender("M")
                .build();

        // when
        MemberResponse memberResponse = memberService.registerMember(request, termsAgreements);

        // then
        Member savedMember = memberRepository.findById(memberResponse.getNo()).orElse(null);

        System.out.println("savedMember.getPwd() = " + savedMember.getPwd());

        assertThat(savedMember).isNotNull();
        assertThat(savedMember.getName()).isEqualTo("홍길동");
        assertThat(savedMember.getPhoneNum()).isEqualTo("010-1234-5678");
        assertThat(bCryptPasswordEncoder.matches("ValidPassword123!", savedMember.getPwd())).isTrue();
    }

}