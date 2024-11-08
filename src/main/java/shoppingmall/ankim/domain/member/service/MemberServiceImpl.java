package shoppingmall.ankim.domain.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shoppingmall.ankim.domain.member.controller.request.MemberRegisterRequest;
import shoppingmall.ankim.domain.member.exception.MemberRegistrationException;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.member.service.request.MemberRegisterServiceRequest;
import shoppingmall.ankim.global.exception.ErrorCode;

import static shoppingmall.ankim.global.exception.ErrorCode.EMAIL_DUPLICATE;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    MemberRepository memberRepository;

    private String verifiedEmailId; // 인증된 이메일 ID 저장

    // 이메일 중복 검증
    public Boolean emailCheck(String id) {
        boolean isExist = memberRepository.existsById(id);
        if (isExist) {
            throw new MemberRegistrationException(EMAIL_DUPLICATE);
        }
        return isExist;
    }

        /*
        * 회원 가입 순서
        * 1. View --- MemberRegisterRequest ---> Controller
        * 2. Controller --- MemberRegisterServiceRequest ---> Service
        * 3. 비밀번호 암호화
        * 4. UUID 생성
        * 5. Entity로 변환
        * 6. insert
        * */
    // 회원가입 로직
    public Boolean registerMember(MemberRegisterServiceRequest request) {

        // 성공적으로 저장했다면 true 반환
        return true;
    }
}
