package shoppingmall.ankim.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.member.controller.request.MemberRegisterRequest;
import shoppingmall.ankim.domain.member.exception.MemberRegistrationException;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.member.service.request.MemberRegisterServiceRequest;
import shoppingmall.ankim.global.exception.ErrorCode;

import static shoppingmall.ankim.global.exception.ErrorCode.EMAIL_DUPLICATE;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

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
    public Boolean registerMember(MemberRegisterServiceRequest request) {

        // 성공적으로 저장했다면 true 반환
        return true;
    }
}
