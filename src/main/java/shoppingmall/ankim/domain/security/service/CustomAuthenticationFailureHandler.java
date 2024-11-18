package shoppingmall.ankim.domain.security.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import shoppingmall.ankim.domain.login.entity.BaseLoginAttempt;
import shoppingmall.ankim.domain.login.entity.member.loginHistory.MemberLoginAttempt;
import shoppingmall.ankim.domain.login.repository.member.MemberLoginAttemptRepository;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.repository.MemberRepository;

import java.io.IOException;
import java.time.LocalDateTime;

// 로그인 실패했을 때 로직 처리하는 곳
@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final MemberRepository memberRepository;
    private final MemberLoginAttemptRepository memberLoginAttemptRepository;

    @Value("${login.attempt.max}")
    private int MAX_LOGIN_ATTEMPTS; // 최대 로그인 시도 횟수

    @Value("${login.lock.time}")
    private int LOCK_TIME_MINUTES; // 잠금 시간 (분)


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {


        String username = request.getParameter("username");

        // 회원 정보 조회
        Member member = memberRepository.findByLoginId(username);

        // 비밀번호가 틀린 경우 처리
        if (exception instanceof BadCredentialsException && member != null) {
            // 로그인 시도 정보 가져오기
            MemberLoginAttempt loginAttempt = memberLoginAttemptRepository
                    .findByMemberAndLoginAttemptDetailsActiveYn(member, "Y")
                    .orElseGet(() -> createNewLoginAttempt(member)); // 없으면 새로 생성

            // 실패 횟수 증가
            loginAttempt.increaseFailCount();

            // 최대 시도 횟수 초과 시 계정 잠금 및 unlockTime 설정
            if (loginAttempt.isUnlockTimePassed()) {
                member.activate();
                loginAttempt.deactivateLoginAttempt();
                memberRepository.save(member);
            } else {
                // 실패 횟수 증가
                loginAttempt.increaseFailCount();

                // 최대 시도 초과 시 계정 잠금 처리
                if (loginAttempt.getLoginAttemptDetails().getFailCount() >= MAX_LOGIN_ATTEMPTS) {
                    member.lock();
                    loginAttempt.setLockTime(LOCK_TIME_MINUTES);
                    memberRepository.save(member);
                }
            }

            // 로그인 시도 업데이트
            memberLoginAttemptRepository.save(loginAttempt);
        }

        // 응답 처리
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        if (exception instanceof BadCredentialsException) {
            response.getWriter().write("비밀번호가 일치하지 않습니다.");
        } else {
            response.getWriter().write("인증 실패: " + exception.getMessage());
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
        return memberLoginAttemptRepository.save(loginAttempt);
    }
}
