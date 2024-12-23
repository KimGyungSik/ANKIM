package shoppingmall.ankim.factory;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.terms.entity.TermsCategory;
import shoppingmall.ankim.domain.termsHistory.entity.TermsHistory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TermsHistoryFactory {

    public static List<TermsHistory> create(EntityManager entityManager, String loginId) {
        TermsCategory category = TermsCategory.JOIN;

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

        entityManager.persist(mainTerms);

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

        entityManager.persist(subTerm1);

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

        entityManager.persist(sub1SubTerm1);

        Terms subTerm2 = Terms.builder()
                .parentTerms(mainTerms)
                .name("마케팅 수신 동의")
                .category(category)
                .contents("마케팅 목적의 개인정보 수집 및 이용 동의")
                .termsYn("Y")
                .termsVersion("v1")
                .level(2)
                .activeYn("Y")
                .build();

        entityManager.persist(subTerm2);

        Terms sub2SubTerm = Terms.builder()
                .parentTerms(subTerm2)
                .name("광고 수신 동의")
                .category(category)
                .contents("광고성 연락 수신 동의")
                .termsYn("Y")
                .termsVersion("v1")
                .level(3)
                .activeYn("Y")
                .build();

        entityManager.persist(sub2SubTerm);

        Terms subSub2SubTerm1 = Terms.builder()
                .parentTerms(sub2SubTerm)
                .name("문자 수신 동의")
                .category(category)
                .contents("광고성 문자 수신 동의")
                .termsYn("Y")
                .termsVersion("v1")
                .level(4)
                .activeYn("Y")
                .build();

        entityManager.persist(subSub2SubTerm1);

        Terms subSub2SubTerm2 = Terms.builder()
                .parentTerms(sub2SubTerm)
                .name("이메일 수신 동의")
                .category(category)
                .contents("광고성 이메일 수신 동의")
                .termsYn("Y")
                .termsVersion("v1")
                .level(4)
                .activeYn("Y")
                .build();

        entityManager.persist(subSub2SubTerm2);

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

        entityManager.persist(mainTerms2);

        Member member = MemberFactory.createMember(entityManager, loginId);

        // 약관 동의
        LocalDateTime now = LocalDateTime.now();

        List<TermsHistory> termsAgreements = new ArrayList<>();

        TermsHistory termsAgreement1 = TermsHistory.builder()
                .member(member)
                .terms(sub1SubTerm1) // 만 14세 이상
                .agreeDate(now)
                .build();

        entityManager.persist(termsAgreement1);
        termsAgreements.add(termsAgreement1);

//        TermsHistory termsAgreement2 = TermsHistory.builder()
//                .member(member)
//                .terms(subSub2SubTerm1) // 문자 수신 동의
//                .agreeDate(now)
//                .build();
//
//        entityManager.persist(termsAgreement2);
//        termsAgreements.add(termsAgreement2);
//
//        entityManager.flush();
//        entityManager.clear();

        return termsAgreements;
    }

}