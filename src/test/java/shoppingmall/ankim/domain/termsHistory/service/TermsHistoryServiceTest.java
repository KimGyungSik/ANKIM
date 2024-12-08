package shoppingmall.ankim.domain.termsHistory.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.terms.repository.TermsRepository;
import shoppingmall.ankim.domain.termsHistory.dto.TermsHistoryUpdateResponse;
import shoppingmall.ankim.domain.termsHistory.entity.TermsHistory;
import shoppingmall.ankim.domain.termsHistory.repository.TermsHistoryRepository;
import shoppingmall.ankim.domain.termsHistory.service.request.TermsUpdateServiceRequest;
import shoppingmall.ankim.factory.TermsHistoryFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
@TestPropertySource(properties = "spring.sql.init.mode=never")
class TermsHistoryServiceTest {

    @Autowired
    private TermsHistoryService termsHistoryService;

    @Autowired
    private TermsRepository termsRepository;

    @Autowired
    private TermsHistoryRepository termsHistoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("회원가입 시 초기 약관 동의 이력을 확인하고 기존에 동의한 약관을 철회한다.")
    void testCreateNewTermsHistory() {
        // given
        String loginId = "test@example.com";
        List<TermsHistory> initialTermsHistories = TermsHistoryFactory.create(em, loginId);

        // 초기 데이터 검증
        assertThat(initialTermsHistories).isNotNull();
        assertThat(initialTermsHistories.size()).isEqualTo(1);

        TermsHistory initialAgreement = initialTermsHistories.get(0);

        // 기존에 동의한 내용을 철회하는 요청
        TermsUpdateServiceRequest updateRequest = TermsUpdateServiceRequest.builder()
                .terms_no(initialAgreement.getTerms().getNo())
                .terms_hist_no(initialAgreement.getNo())
                .terms_hist_agreeYn("N") // 철회
                .build();

        // when : 약관동의 업데이트
        termsHistoryService.updateTermsAgreement(loginId, List.of(updateRequest));
        Member member = memberRepository.findByLoginId(loginId);

        // then : 새롭게 생성된 약관 동의 이력을 확인
        Optional<TermsHistory> newHistory = termsHistoryRepository.findActiveByMemberAndTerms(member.getNo(), initialAgreement.getTerms().getNo());

        assertThat(newHistory).isPresent();
        assertThat(newHistory.get().getAgreeYn()).isEqualTo("N");
    }

    @Test
    @DisplayName("회원가입 시 초기 약관 동의 이력을 확인하고 약관을 여러개 동의하는 경우 모두 동의 이력이 남는다.")
    public void testActivateParentTermsOnInitialAgreement() {
        // given
        String loginId = "test@example.com";
        List<TermsHistory> initialTermsHistories = TermsHistoryFactory.create(em, loginId);

        em.flush();
        em.clear();

        // 초기 데이터 검증
        assertThat(initialTermsHistories).isNotNull();
        assertThat(initialTermsHistories.size()).isEqualTo(1);

        // 마케팅과 문자 키워드로 약관 추출
        Terms marketingTerms = termsRepository.findTermsByNameKeyword("마케팅").stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("마케팅 약관이 생성되지 않았습니다."));

        Terms smsTerms = termsRepository.findTermsByNameKeyword("문자").stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("문자 약관이 생성되지 않았습니다."));

        // 마케팅 목적 동의
        List<TermsUpdateServiceRequest> updateRequestList = new ArrayList<>();

        updateRequestList.add(TermsUpdateServiceRequest.builder()
                .terms_no(marketingTerms.getNo())
                .terms_hist_no(null)
                .terms_hist_agreeYn("Y") // 마케팅 동의
                .build());
        updateRequestList.add(TermsUpdateServiceRequest.builder()
                .terms_no(smsTerms.getNo())
                .terms_hist_no(null)
                .terms_hist_agreeYn("Y") // 문자 수신 동의
                .build());

        // when
        termsHistoryService.updateTermsAgreement(loginId, updateRequestList);

        assertThat(marketingTerms).isNotNull();
        assertThat(smsTerms).isNotNull();

        // then
        Member member = memberRepository.findByLoginId(loginId);

        Optional<TermsHistory> marketingHistory = termsHistoryRepository.findActiveByMemberAndTerms(member.getNo(), marketingTerms.getNo());
        Optional<TermsHistory> smsHistory = termsHistoryRepository.findActiveByMemberAndTerms(member.getNo(), smsTerms.getNo());

        assertThat(marketingHistory).isPresent();
        assertThat(smsHistory).isPresent();
        assertThat(marketingHistory.get().getAgreeYn()).isEqualTo("Y");
        assertThat(smsHistory.get().getAgreeYn()).isEqualTo("Y");
    }

    @Test
    @DisplayName("마케팅 약관과 문자 수신 동의 약관을 동의한 뒤 취소하는 경우 이력에 취소한 기록이 남는다.")
    public void testRevokeMarketingAndSmsAgreement() throws Exception {
        // given
        String loginId = "test@example.com";
        TermsHistoryFactory.create(em, loginId);

        // flush 및 clear로 데이터 동기화
        em.flush();
        em.clear();

        // 마케팅과 문자 키워드로 약관 추출
        Terms marketingTerms = termsRepository.findTermsByNameKeyword("마케팅").stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("마케팅 약관이 생성되지 않았습니다."));

        Terms smsTerms = termsRepository.findTermsByNameKeyword("문자").stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("문자 약관이 생성되지 않았습니다."));

        assertThat(marketingTerms).isNotNull();
        assertThat(smsTerms).isNotNull();

        // 마케팅과 문자 동의 요청 생성
        List<TermsUpdateServiceRequest> agreementRequestList = new ArrayList<>();
        agreementRequestList.add(TermsUpdateServiceRequest.builder()
                .terms_no(marketingTerms.getNo())
                .terms_hist_no(null)
                .terms_hist_agreeYn("Y") // 마케팅 동의
                .build());
        agreementRequestList.add(TermsUpdateServiceRequest.builder()
                .terms_no(smsTerms.getNo())
                .terms_hist_no(null)
                .terms_hist_agreeYn("Y") // 문자 수신 동의
                .build());

        // when : 마케팅과 문자 약관 동의
        termsHistoryService.updateTermsAgreement(loginId, agreementRequestList);

        // flush 및 clear로 동기화
        em.flush();
        em.clear();

        // 마케팅과 문자 동의 취소 요청 생성
        List<TermsUpdateServiceRequest> revokeRequestList = new ArrayList<>();
        revokeRequestList.add(TermsUpdateServiceRequest.builder()
                .terms_no(marketingTerms.getNo())
                .terms_hist_no(null)
                .terms_hist_agreeYn("N") // 마케팅 철회
                .build());
        revokeRequestList.add(TermsUpdateServiceRequest.builder()
                .terms_no(smsTerms.getNo())
                .terms_hist_no(null)
                .terms_hist_agreeYn("N") // 문자 수신 철회
                .build());

        // when : 마케팅과 문자 약관 철회
        termsHistoryService.updateTermsAgreement(loginId, revokeRequestList);

        // flush 및 clear로 동기화
        em.flush();
        em.clear();

        // then : 마케팅과 문자 약관이 철회되었는지 확인
        Member member = memberRepository.findByLoginId(loginId);

        // 철회된 기록이 이력에 남았는지 확인
        Optional<TermsHistory> marketingHistory = termsHistoryRepository.findActiveByMemberAndTerms(member.getNo(), marketingTerms.getNo());
        Optional<TermsHistory> smsHistory = termsHistoryRepository.findActiveByMemberAndTerms(member.getNo(), smsTerms.getNo());

        assertThat(marketingHistory).isPresent();
        assertThat(smsHistory).isPresent();
        assertThat(marketingHistory.get().getAgreeYn()).isEqualTo("N");
        assertThat(smsHistory.get().getAgreeYn()).isEqualTo("N");
    }

    @Test
    @DisplayName("마케팅 약관, 문자 수신 동의 약관, 이메일 수신 동의 약관을 동의한 뒤 문자, 이메일 수신을 취소하는 경우 이력에 취소한 기록이 남는다.")
    public void testRevokeMarketingSmsAndEmailAgreement() throws Exception {
        // given
        String loginId = "test@example.com";
        TermsHistoryFactory.create(em, loginId);

        // flush 및 clear로 데이터 동기화
        em.flush();
        em.clear();

        // 마케팅, 문자, 이메일 키워드로 약관 추출
        Terms marketingTerms = termsRepository.findTermsByNameKeyword("마케팅").stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("마케팅 약관이 생성되지 않았습니다."));

        Terms smsTerms = termsRepository.findTermsByNameKeyword("문자").stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("문자 약관이 생성되지 않았습니다."));

        Terms emailTerms = termsRepository.findTermsByNameKeyword("이메일").stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("이메일 약관이 생성되지 않았습니다."));

        assertThat(marketingTerms).isNotNull();
        assertThat(smsTerms).isNotNull();
        assertThat(emailTerms).isNotNull();

        // 마케팅, 문자, 이메일 동의 요청 생성
        List<TermsUpdateServiceRequest> agreementRequestList = new ArrayList<>();
        agreementRequestList.add(TermsUpdateServiceRequest.builder()
                .terms_no(marketingTerms.getNo())
                .terms_hist_no(null)
                .terms_hist_agreeYn("Y") // 마케팅 동의
                .build());
        agreementRequestList.add(TermsUpdateServiceRequest.builder()
                .terms_no(smsTerms.getNo())
                .terms_hist_no(null)
                .terms_hist_agreeYn("Y") // 문자 수신 동의
                .build());
        agreementRequestList.add(TermsUpdateServiceRequest.builder()
                .terms_no(emailTerms.getNo())
                .terms_hist_no(null)
                .terms_hist_agreeYn("Y") // 이메일 동의
                .build());

        // when : 마케팅, 문자, 이메일 약관 동의
        TermsHistoryUpdateResponse termsHistoryUpdateResponse = termsHistoryService.updateTermsAgreement(loginId, agreementRequestList);
        assertThat(termsHistoryUpdateResponse).isNotNull();
        assertThat(termsHistoryUpdateResponse.getMessage().size()).isEqualTo(agreementRequestList.size());

        // 메시지 검증
        List<String> expectedAgreementMessages = List.of(
                marketingTerms.getName() + " 동의가 완료되었습니다.",
                smsTerms.getName() + " 동의가 완료되었습니다.",
                emailTerms.getName() + " 동의가 완료되었습니다."
        );
        for (int i = 0; i < termsHistoryUpdateResponse.getMessage().size(); i++) {
            assertThat(termsHistoryUpdateResponse.getMessage().get(i)).isEqualTo(expectedAgreementMessages.get(i));
        }

        // flush 및 clear로 동기화
        em.flush();
        em.clear();

        // then : 동의 여부 확인
        Member member = memberRepository.findByLoginId(loginId);

        Optional<TermsHistory> marketingHistory = termsHistoryRepository.findActiveByMemberAndTerms(member.getNo(), marketingTerms.getNo());
        Optional<TermsHistory> smsHistory = termsHistoryRepository.findActiveByMemberAndTerms(member.getNo(), smsTerms.getNo());
        Optional<TermsHistory> emailHistory = termsHistoryRepository.findActiveByMemberAndTerms(member.getNo(), emailTerms.getNo());

        // 동의한 상태 확인
        assertThat(marketingHistory).isPresent();
        assertThat(smsHistory).isPresent();
        assertThat(emailHistory).isPresent();
        assertThat(marketingHistory.get().getAgreeYn()).isEqualTo("Y");
        assertThat(smsHistory.get().getAgreeYn()).isEqualTo("Y");
        assertThat(emailHistory.get().getAgreeYn()).isEqualTo("Y");

        // 마케팅, 문자, 이메일 동의 취소 요청 생성
        List<TermsUpdateServiceRequest> revokeRequestList = new ArrayList<>();
        revokeRequestList.add(TermsUpdateServiceRequest.builder()
                .terms_no(smsTerms.getNo())
                .terms_hist_no(null)
                .terms_hist_agreeYn("N") // 문자 수신 철회
                .build());
        revokeRequestList.add(TermsUpdateServiceRequest.builder()
                .terms_no(emailTerms.getNo())
                .terms_hist_no(null)
                .terms_hist_agreeYn("N") // 이메일 철회
                .build());

        // when : 마케팅, 문자, 이메일 약관 철회
        TermsHistoryUpdateResponse termsHistoryRevokeResponse = termsHistoryService.updateTermsAgreement(loginId, revokeRequestList);
        assertThat(termsHistoryRevokeResponse).isNotNull();
        assertThat(termsHistoryRevokeResponse.getMessage().size()).isEqualTo(revokeRequestList.size());

        // 철회 메시지 검증
        List<String> expectedRevokeMessages = List.of(
                smsTerms.getName() + " 거부가 완료되었습니다.",
                emailTerms.getName() + " 거부가 완료되었습니다."
        );
        for (int i = 0; i < termsHistoryRevokeResponse.getMessage().size(); i++) {
            assertThat(termsHistoryRevokeResponse.getMessage().get(i)).isEqualTo(expectedRevokeMessages.get(i));
        }

        // flush 및 clear로 동기화
        em.flush();
        em.clear();

        // then : 마케팅, 문자, 이메일 약관이 철회되었는지 확인
        marketingHistory = termsHistoryRepository.findActiveByMemberAndTerms(member.getNo(), marketingTerms.getNo());
        smsHistory = termsHistoryRepository.findActiveByMemberAndTerms(member.getNo(), smsTerms.getNo());
        emailHistory = termsHistoryRepository.findActiveByMemberAndTerms(member.getNo(), emailTerms.getNo());

        assertThat(marketingHistory).isPresent();
        assertThat(smsHistory).isPresent();
        assertThat(emailHistory).isPresent();
        assertThat(marketingHistory.get().getAgreeYn()).isEqualTo("Y");
        assertThat(smsHistory.get().getAgreeYn()).isEqualTo("N");
        assertThat(emailHistory.get().getAgreeYn()).isEqualTo("N");
    }

    @Test
    @DisplayName("문자 수신 동의와 이메일 수신 동의를 동의하는 경우 광고성 연락 수신 약관도 동의로 처리된다.")
    public void testAgreeToMarketingContactTermsWhenSmsAndEmailAgreed() throws Exception {
        // given
        String loginId = "test@example.com";
        TermsHistoryFactory.create(em, loginId);

        em.flush();
        em.clear();

        // 약관 키워드로 약관 추출
        Terms smsTerms = termsRepository.findTermsByNameKeyword("문자").stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("문자 약관이 생성되지 않았습니다."));

        Terms emailTerms = termsRepository.findTermsByNameKeyword("이메일").stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("이메일 약관이 생성되지 않았습니다."));

        Terms marketingContactTerms = termsRepository.findTermsByNameKeyword("광고").stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("광고성 연락 약관이 생성되지 않았습니다."));

        assertThat(smsTerms).isNotNull();
        assertThat(emailTerms).isNotNull();
        assertThat(marketingContactTerms).isNotNull();

        // 문자와 이메일 동의 요청 생성
        List<TermsUpdateServiceRequest> agreementRequestList = new ArrayList<>();
        agreementRequestList.add(TermsUpdateServiceRequest.builder()
                .terms_no(smsTerms.getNo())
                .terms_hist_no(null)
                .terms_hist_agreeYn("Y")
                .build());
        agreementRequestList.add(TermsUpdateServiceRequest.builder()
                .terms_no(emailTerms.getNo())
                .terms_hist_no(null)
                .terms_hist_agreeYn("Y")
                .build());

        // when : 문자와 이메일 동의 처리
        termsHistoryService.updateTermsAgreement(loginId, agreementRequestList);

        em.flush();
        em.clear();

        // then : 광고성 연락 수신 약관도 동의로 처리되었는지 확인
        Member member = memberRepository.findByLoginId(loginId);
        Optional<TermsHistory> smsHistory = termsHistoryRepository.findActiveByMemberAndTerms(member.getNo(), smsTerms.getNo());
        Optional<TermsHistory> emailHistory = termsHistoryRepository.findActiveByMemberAndTerms(member.getNo(), emailTerms.getNo());
        Optional<TermsHistory> marketingContactHistory = termsHistoryRepository.findActiveByMemberAndTerms(member.getNo(), marketingContactTerms.getNo());

        assertThat(smsHistory).isPresent();
        assertThat(emailHistory).isPresent();
        assertThat(marketingContactHistory).isPresent();
        assertThat(smsHistory.get().getAgreeYn()).isEqualTo("Y");
        assertThat(emailHistory.get().getAgreeYn()).isEqualTo("Y");
        assertThat(marketingContactHistory.get().getAgreeYn()).isEqualTo("Y");
    }

    @Test
    @DisplayName("문자 수신 또는 이메일 수신 동의를 철회하는 경우 광고성 연락 수신 약관도 철회된다.")
    public void testRevokeMarketingContactTermsWhenSmsOrEmailRevoked() throws Exception {
        // given
        String loginId = "test@example.com";
        TermsHistoryFactory.create(em, loginId);

        em.flush();
        em.clear();

        // 약관 키워드로 약관 추출
        Terms smsTerms = termsRepository.findTermsByNameKeyword("문자").stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("문자 약관이 생성되지 않았습니다."));

        Terms emailTerms = termsRepository.findTermsByNameKeyword("이메일").stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("이메일 약관이 생성되지 않았습니다."));

        Terms marketingContactTerms = termsRepository.findTermsByNameKeyword("광고").stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("광고성 연락 약관이 생성되지 않았습니다."));

        assertThat(smsTerms).isNotNull();
        assertThat(emailTerms).isNotNull();
        assertThat(marketingContactTerms).isNotNull();

        // 문자와 이메일 동의 요청 생성
        List<TermsUpdateServiceRequest> agreementRequestList = new ArrayList<>();
        agreementRequestList.add(TermsUpdateServiceRequest.builder()
                .terms_no(smsTerms.getNo())
                .terms_hist_no(null)
                .terms_hist_agreeYn("Y")
                .build());
        agreementRequestList.add(TermsUpdateServiceRequest.builder()
                .terms_no(emailTerms.getNo())
                .terms_hist_no(null)
                .terms_hist_agreeYn("Y")
                .build());

        // 문자와 이메일 동의 처리
        termsHistoryService.updateTermsAgreement(loginId, agreementRequestList);

        em.flush();
        em.clear();

        // 문자 동의 철회 요청 생성
        List<TermsUpdateServiceRequest> revokeRequestList = new ArrayList<>();
        revokeRequestList.add(TermsUpdateServiceRequest.builder()
                .terms_no(smsTerms.getNo())
                .terms_hist_no(null)
                .terms_hist_agreeYn("N")
                .build());

        // when : 문자 동의 철회 처리
        termsHistoryService.updateTermsAgreement(loginId, revokeRequestList);

        em.flush();
        em.clear();

        // then : 광고성 연락 수신 약관도 철회되었는지 확인
        Member member = memberRepository.findByLoginId(loginId);
        Optional<TermsHistory> smsHistory = termsHistoryRepository.findActiveByMemberAndTerms(member.getNo(), smsTerms.getNo());
        Optional<TermsHistory> emailHistory = termsHistoryRepository.findActiveByMemberAndTerms(member.getNo(), emailTerms.getNo());
        Optional<TermsHistory> marketingContactHistory = termsHistoryRepository.findActiveByMemberAndTerms(member.getNo(), marketingContactTerms.getNo());

        assertThat(smsHistory).isPresent();
        assertThat(emailHistory).isPresent();
        assertThat(marketingContactHistory).isPresent();
        assertThat(smsHistory.get().getAgreeYn()).isEqualTo("N");
        assertThat(marketingContactHistory.get().getAgreeYn()).isEqualTo("N");
    }
}