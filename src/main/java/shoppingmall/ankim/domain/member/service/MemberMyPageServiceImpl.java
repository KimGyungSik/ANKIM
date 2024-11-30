package shoppingmall.ankim.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.member.dto.MemberResponse;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.exception.InvalidMemberException;
import shoppingmall.ankim.domain.member.exception.MemberRegistrationException;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.member.service.request.ChangePasswordServiceRequest;
import shoppingmall.ankim.domain.member.service.request.MemberRegisterServiceRequest;
import shoppingmall.ankim.domain.security.exception.JwtValidException;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;
import shoppingmall.ankim.domain.terms.entity.Terms;
import shoppingmall.ankim.domain.terms.service.query.TermsQueryService;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsAgreement;

import java.util.List;

import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberMyPageServiceImpl implements MemberMyPageService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void isValidPassword(String accessToken, String pwd) {
        Member member = getMember(accessToken);

        // 입력된 비밀번호와 저장된 비밀번호 해시 비교
        boolean isPasswordValid = bCryptPasswordEncoder.matches(pwd, member.getPwd());
        if (!isPasswordValid) {
            throw new InvalidMemberException(INVALID_PASSWORD);
        }
    }

    /*
    * TODO
    * [비밀번호 변경]
    *
    * */
    @Override
    public void changePassword(String accessToken, ChangePasswordServiceRequest request) {

    }

    private Member getMember(String accessToken) {
        // 토큰 유효성 검사(만료 검사도 들어있음)
        if (!jwtTokenProvider.isTokenValidate(accessToken)) {
            throw new JwtValidException(TOKEN_VALIDATION_ERROR);
        }
        // member의 loginId 추출
        String loginId = jwtTokenProvider.getUsernameFromToken(accessToken);
        // loginId를 가지고 member엔티티의 no 조회
        Member member = memberRepository.findByLoginId(loginId);
        if (member == null) {
            throw new InvalidMemberException(INVALID_MEMBER);
        }
        return member;
    }
}
