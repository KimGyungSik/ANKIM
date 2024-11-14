package shoppingmall.ankim.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.member.dto.MemberResponse;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.exception.MemberRegistrationException;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.member.service.request.MemberRegisterServiceRequest;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.terms.service.TermsService;
import shoppingmall.ankim.domain.terms.service.query.TermsQueryService;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsAgreement;

import java.util.List;

import static shoppingmall.ankim.global.exception.ErrorCode.EMAIL_DUPLICATE;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final TermsQueryService termsQueryService;

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private String verifiedEmailId; // 인증된 이메일 ID 저장

    // 이메일 중복 검증
    public void emailCheck(String id) {
        if (memberRepository.existsById(id)) {
            throw new MemberRegistrationException(EMAIL_DUPLICATE);
        }
    }

    /*
    * 회원 가입 순서
    * 1. Member 엔티티 관련
    *   1.1. View --- MemberRegisterRequest ---> Controller
    *   1.2. Controller --- MemberRegisterServiceRequest ---> Service
    *   1.3. 비밀번호 암호화
    *   1.4. UUID 생성
    *   1.5. Entity로 변환
    *   1.6. Member 테이븡에 insert
    * 2. Terms & TermsHistory 엔티티 관련
    *   2.1. View --- TermsHistoryRegisterRequest ---> Controller
    *   2.2. Controller --- TermsHistoryRegisterServiceRequest ---> Service
    *   2.3. Entity로 변환
    *   2.4. TermsHistory 테이블에 insert
    * */
    // 회원가입 로직
    public MemberResponse registerMember(MemberRegisterServiceRequest request, List<TermsAgreement> termsAgreements) {
        // termsAgreements를 Terms로 변환
        List<Terms> agreeTerms = termsQueryService.validateAndAddSubTerms(termsAgreements);

        // 비밀번호 암호화
        String encodePwd = bCryptPasswordEncoder.encode(request.getPwd());

        Member member = request.create(encodePwd, agreeTerms);
        memberRepository.save(member); // 회원가입과 동시에 TermsHistory도 저장!

        log.info("회원가입 완료");
        return MemberResponse.of(member);
    }
}
