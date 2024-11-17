package shoppingmall.ankim.domain.login.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import shoppingmall.ankim.domain.login.exception.LoginFailedException;
import shoppingmall.ankim.domain.login.service.request.LoginServiceRequest;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;
import shoppingmall.ankim.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private BCryptPasswordEncoder bcryptPasswordEncoder;

    public LoginServiceImpl(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void login(LoginServiceRequest loginServiceRequest) {
        // 사용자 조회 (status에 따라서 상태를 반환해줘야 됨)
        Member member = memberRepository.findByLoginIdExcludingWithdrawn(loginServiceRequest.getLoginId());

        // 회원 계정이 잠김 상태인 경우
        if (member.getStatus() == MemberStatus.LOCKED) {
            throw new LoginFailedException(ErrorCode.MEMBER_STATUS_LOCKED);
        }

        // 비밀번호 검증
        // 사용자 세부 정보 생성 (CustomUserDetails)
        // Jwt 토큰 생성

    }

    public void findByLoginId(String loginId) {
        memberRepository.findByLoginId(loginId);
    }

}
