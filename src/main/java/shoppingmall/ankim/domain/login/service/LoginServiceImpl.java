package shoppingmall.ankim.domain.login.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import shoppingmall.ankim.domain.login.entity.BaseLoginAttempt;
import shoppingmall.ankim.domain.login.entity.member.loginHistory.MemberLoginAttempt;
import shoppingmall.ankim.domain.login.exception.LoginFailedException;
import shoppingmall.ankim.domain.login.repository.member.MemberLoginAttemptRepository;
import shoppingmall.ankim.domain.login.service.request.LoginServiceRequest;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.security.dto.CustomUserDetails;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;

import java.time.LocalDateTime;

import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final MemberRepository memberRepository;
    private final MemberLoginAttemptRepository loginAttemptRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bcryptPasswordEncoder;

    @Value("${login.attempt.max}")
    private int MAX_LOGIN_ATTEMPTS; // 최대 로그인 시도 횟수

    @Value("${login.lock.time}")
    private int LOCK_TIME_MINUTES; // 잠금 시간 (분)

    @Override
    public String login(LoginServiceRequest loginServiceRequest) {
        // 사용자 조회 (status에 따라서 상태를 반환해줘야 됨)
        Member member = memberRepository.findByLoginIdExcludingWithdrawn(loginServiceRequest.getLoginId());

        // 사용자가 없는 경우
        if(member == null) {
            throw new LoginFailedException(MEMBER_NOT_FOUND);
        }

        // 로그인 시도 기록 가져오기
        MemberLoginAttempt loginAttempt = loginAttemptRepository
                .findByMemberAndLoginAttemptDetailsActiveYn(member, "Y")
                .orElseGet(() -> createNewLoginAttempt(member));

        // 회원 계정이 잠김 상태인 경우
        if (member.getStatus() == MemberStatus.LOCKED && !loginAttempt.isUnlockTimePassed()) {
            throw new LoginFailedException(MEMBER_STATUS_LOCKED);
        }

        // 비밀번호 검증
        // 사용자 세부 정보 생성 (CustomUserDetails)
        // Jwt 토큰 생성
        String username = loginServiceRequest.getLoginId();
        String password = loginServiceRequest.getPwd();
        System.out.println("username = " + username);
        System.out.println("password = " + password);
        try {
            // Spring Security를 통해 인증 시도
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // 인증 성공 시 실패 기록 초기화
            loginAttempt.resetFailCount();
            loginAttemptRepository.save(loginAttempt);

            // JWT 생성 및 반환
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return jwtTokenProvider.generateAccessToken(userDetails);

        } catch (BadCredentialsException ex) {
            // 실패 시 처리
            handleLoginFailure(member, loginAttempt);
            throw new LoginFailedException(INVALID_CREDENTIALS);
        }
    }

    private void handleLoginFailure(Member member, MemberLoginAttempt loginAttempt) {
        // 실패 횟수 증가
        loginAttempt.increaseFailCount();
        loginAttemptRepository.save(loginAttempt);

        // 최대 실패 횟수 초과 시 계정 잠금
        if (loginAttempt.getLoginAttemptDetails().getFailCount() >= MAX_LOGIN_ATTEMPTS) {
            member.lock();
            loginAttempt.setLockTime(LOCK_TIME_MINUTES);
            memberRepository.save(member);
        }
    }

    // 새로운 로그인 시도 객체 생성
    private MemberLoginAttempt createNewLoginAttempt(Member member) {
        MemberLoginAttempt loginAttempt = MemberLoginAttempt.builder()
                .member(member)
                .loginAttemptDetails(BaseLoginAttempt.builder()
                        .failCount(0)
                        .lastAttemptTime(LocalDateTime.now())
                        .unlockTime(null)
                        .activeYn("Y")
                        .build())
                .build();
        return loginAttemptRepository.save(loginAttempt);
    }

}
