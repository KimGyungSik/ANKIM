package shoppingmall.ankim.domain.security.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import shoppingmall.ankim.domain.login.entity.BaseLoginAttempt;
import shoppingmall.ankim.domain.login.entity.member.loginHistory.MemberLoginAttempt;
import shoppingmall.ankim.domain.login.repository.member.MemberLoginAttemptRepository;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.member.repository.MemberRepository;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

class CustomAuthenticationFailureHandlerTest {

    @InjectMocks
    private CustomAuthenticationFailureHandler failureHandler;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberLoginAttemptRepository memberLoginAttemptRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("비밀번호가 틀린 경우 로그인 시도가 증가하고 상태가 업데이트된다.")
    void testOnAuthenticationFailure_IncreaseFailCount() throws Exception {
        // given
        String username = "test@example.com";
        when(request.getParameter("username")).thenReturn(username);
        when(response.getWriter()).thenReturn(printWriter);

        Member member = Member.builder()
                .loginId(username)
                .pwd("password")
                .status(MemberStatus.ACTIVE)
                .build();
        when(memberRepository.findByLoginId(username)).thenReturn(member);

        BaseLoginAttempt loginAttemptDetails = BaseLoginAttempt.builder()
                .failCount(1)
                .lastAttemptTime(LocalDateTime.now())
                .unlockTime(null)
                .activeYn("Y")
                .build();

        MemberLoginAttempt loginAttempt = MemberLoginAttempt.builder()
                .member(member)
                .loginAttemptDetails(loginAttemptDetails)
                .build();

        when(memberLoginAttemptRepository.findByMemberAndLoginAttemptDetailsActiveYn(member, "Y"))
                .thenReturn(Optional.of(loginAttempt));

        // when
        failureHandler.onAuthenticationFailure(request, response, new BadCredentialsException("Bad credentials"));

        // then
        verify(memberLoginAttemptRepository, times(1)).save(any(MemberLoginAttempt.class));
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(printWriter).write("비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("최대 로그인 시도 초과 시 계정이 잠기고 unlockTime이 설정된다.")
    void testOnAuthenticationFailure_AccountLock() throws Exception {
        // given
        String username = "lockeduser@example.com";
        when(request.getParameter("username")).thenReturn(username);
        when(response.getWriter()).thenReturn(printWriter);

        Member member = Member.builder()
                .loginId(username)
                .pwd("password")
                .status(MemberStatus.ACTIVE)
                .build();
        when(memberRepository.findByLoginId(username)).thenReturn(member);

        BaseLoginAttempt loginAttemptDetails = BaseLoginAttempt.builder()
                .failCount(5) // 최대 시도 초과
                .lastAttemptTime(LocalDateTime.now())
                .unlockTime(null)
                .activeYn("Y")
                .build();

        MemberLoginAttempt loginAttempt = MemberLoginAttempt.builder()
                .member(member)
                .loginAttemptDetails(loginAttemptDetails)
                .build();

        when(memberLoginAttemptRepository.findByMemberAndLoginAttemptDetailsActiveYn(member, "Y"))
                .thenReturn(Optional.of(loginAttempt));

        // when
        failureHandler.onAuthenticationFailure(request, response, new BadCredentialsException("Bad credentials"));

        // then
        verify(memberRepository, times(1)).save(member);
        verify(memberLoginAttemptRepository, times(1)).save(any(MemberLoginAttempt.class));
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(printWriter).write("비밀번호가 일치하지 않습니다.");
    }
}