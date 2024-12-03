package shoppingmall.ankim.domain.security.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import shoppingmall.ankim.domain.login.entity.BaseLoginAttempt;
import shoppingmall.ankim.domain.login.entity.member.loginHistory.MemberLoginAttempt;
import shoppingmall.ankim.domain.login.repository.member.MemberLoginAttemptRepository;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.memberHistory.repository.MemberHistoryRepository;
import shoppingmall.ankim.domain.security.dto.CustomUserDetails;
import shoppingmall.ankim.domain.security.exception.AccountStatusLockedException;
import shoppingmall.ankim.domain.security.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberHistoryRepository memberHistoryRepository;

    @Mock
    private MemberLoginAttemptRepository memberLoginAttemptRepository;

    private Member member;
    private MemberLoginAttempt loginAttempt;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("성공적으로 사용자 정보를 조회한다.")
    void testLoadUserByUsername_Success() {
        // given
        member = Member.builder()
                .no(2L)
                .loginId("user@example.com")
                .pwd("password")
                .name("Test User")
                .status(MemberStatus.ACTIVE)
                .build();

        BaseLoginAttempt loginAttemptDetails = BaseLoginAttempt.builder()
                .failCount(1)
                .lastAttemptTime(LocalDateTime.now().minusMinutes(10))
                .unlockTime(LocalDateTime.now().minusMinutes(5)) // Unlock 조건 충족
                .activeYn("Y")
                .build();

        loginAttempt = MemberLoginAttempt.builder()
                .member(member)
                .loginAttemptDetails(loginAttemptDetails)
                .build();

        when(memberRepository.findByLoginId("user@example.com")).thenReturn(member);
        when(memberLoginAttemptRepository.findByMemberAndLoginAttemptDetailsActiveYn(member, "Y"))
                .thenReturn(Optional.of(loginAttempt));

        // when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("user@example.com");

        // then
        assertNotNull(userDetails);
        assertTrue(userDetails instanceof CustomUserDetails);
        assertEquals("user@example.com", userDetails.getUsername());
        verify(memberRepository, times(1)).findByLoginId("user@example.com");
        verify(memberLoginAttemptRepository, times(1)).findByMemberAndLoginAttemptDetailsActiveYn(member, "Y");
    }

    @Test
    @DisplayName("존재하지 않는 사용자 조회 시 UserNotFoundException이 발생한다.")
    void testLoadUserByUsername_UserNotFound() {
        // given
        when(memberRepository.findByLoginId("unknown@example.com")).thenReturn(null);

        // when & then
        assertThrows(UserNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername("unknown@example.com"));
        verify(memberRepository, times(1)).findByLoginId("unknown@example.com");
    }

    @Test
    @DisplayName("계정이 잠긴 상태라면 AccountStatusLockedException을 던진다.")
    void testLoadUserByUsername_AccountLocked() {
        // given
        Member lockedMember = Member.builder()
                .loginId("locked@example.com")
                .pwd("password")
                .name("Locked User")
                .status(MemberStatus.LOCKED) // 계정 상태를 잠금으로 설정
                .build();

        BaseLoginAttempt loginAttemptDetails = BaseLoginAttempt.builder()
                .failCount(3) // 최대 실패 횟수 초과
                .lastAttemptTime(LocalDateTime.now().minusMinutes(10))
                .unlockTime(LocalDateTime.now().plusMinutes(5)) // 계정잠김이 풀리는 시간이 아직 안됨
                .activeYn("Y")
                .build();

        MemberLoginAttempt loginAttempt = MemberLoginAttempt.builder()
                .no(1L)
                .member(lockedMember)
                .loginAttemptDetails(loginAttemptDetails)
                .build();

        when(memberRepository.findByLoginId("locked@example.com")).thenReturn(lockedMember);
        when(memberLoginAttemptRepository.findByMemberAndLoginAttemptDetailsActiveYn(lockedMember, "Y"))
                .thenReturn(Optional.of(loginAttempt));

        // when & then
        assertThrows(AccountStatusLockedException.class, () ->
                customUserDetailsService.loadUserByUsername("locked@example.com"));

        verify(memberRepository, times(1)).findByLoginId("locked@example.com");
        verify(memberLoginAttemptRepository, times(1)).findByMemberAndLoginAttemptDetailsActiveYn(lockedMember, "Y");
    }

    @Test
    @DisplayName("unlockTime이 현재 시간보다 이전인 경우 계정을 활성화하고 로그인 시도를 초기화한다.")
    void testLoadUserByUsername_UnlockTimePassed() {
        // given
        member = Member.builder()
                .no(2L)
                .loginId("unlock@example.com")
                .pwd("password")
                .name("Test User")
                .status(MemberStatus.LOCKED)
                .build();

        BaseLoginAttempt loginAttemptDetails = BaseLoginAttempt.builder()
                .failCount(1)
                .lastAttemptTime(LocalDateTime.now().minusMinutes(10))
                .unlockTime(LocalDateTime.now().minusMinutes(5)) // Unlock 조건 충족
                .activeYn("Y")
                .build();

        loginAttempt = MemberLoginAttempt.builder()
                .member(member)
                .loginAttemptDetails(loginAttemptDetails)
                .build();

        when(memberRepository.findByLoginId("unlock@example.com")).thenReturn(member);
        when(memberLoginAttemptRepository.findByMemberAndLoginAttemptDetailsActiveYn(member, "Y"))
                .thenReturn(Optional.of(loginAttempt));

        // when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("unlock@example.com");

        // then
        assertNotNull(userDetails);
        assertEquals(MemberStatus.ACTIVE, member.getStatus());
        verify(memberRepository, times(1)).save(member);
        verify(memberLoginAttemptRepository, times(1)).save(any(MemberLoginAttempt.class));
    }
}