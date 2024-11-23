package shoppingmall.ankim.domain.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
//import shoppingmall.ankim.domain.admin.repository.AdminRepository;
import shoppingmall.ankim.domain.admin.entity.Admin;
import shoppingmall.ankim.domain.admin.entity.AdminStatus;
import shoppingmall.ankim.domain.admin.repository.AdminRepository;
import shoppingmall.ankim.domain.login.entity.BaseLoginAttempt;
import shoppingmall.ankim.domain.login.entity.admin.loginHistory.AdminLoginAttempt;
import shoppingmall.ankim.domain.login.entity.member.loginHistory.MemberLoginAttempt;
import shoppingmall.ankim.domain.login.repository.admin.AdminLoginAttemptRepository;
import shoppingmall.ankim.domain.login.repository.member.MemberLoginAttemptRepository;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.security.dto.CustomUserDetails;
import shoppingmall.ankim.domain.security.exception.AccountStatusLockedException;
import shoppingmall.ankim.domain.security.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

import static shoppingmall.ankim.global.exception.ErrorCode.USER_STATUS_LOCKED;
import static shoppingmall.ankim.global.exception.ErrorCode.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final MemberLoginAttemptRepository memberLoginAttemptRepository;
    private final AdminRepository adminRepository;
    private final AdminLoginAttemptRepository adminLoginAttemptRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$" // 이메일 형식 정규식
    );

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 이메일 형식이면 일반 회원으로 간주
        if (isEmail(username)) {
            // 사용자 정보 조회
            Member member = memberRepository.findByLoginId(username);
            if (member == null) {
                throw new UserNotFoundException(NOT_FOUND_USER);
            }

            // 로그인 시도 정보 확인
            MemberLoginAttempt loginAttempt = memberLoginAttemptRepository
                    .findByMemberAndLoginAttemptDetailsActiveYn(member, "Y")
                    .orElse(null);

            if (loginAttempt != null) {
                if (loginAttempt.getLoginAttemptDetails().getUnlockTime() != null &&
                        loginAttempt.getLoginAttemptDetails().getUnlockTime().isBefore(LocalDateTime.now())) {

                    // UnlockTime이 지났으므로 계정 상태 업데이트 및 초기화
                    member.activate(); // 계정 활성화
                    memberRepository.save(member);

                    MemberLoginAttempt updatedLoginAttempt = MemberLoginAttempt.builder()
                            .no(loginAttempt.getNo())
                            .member(loginAttempt.getMember())
                            .loginAttemptDetails(BaseLoginAttempt.builder()
                                    .failCount(0)
                                    .lastAttemptTime(LocalDateTime.now())
                                    .unlockTime(null)
                                    .activeYn("Y")
                                    .build())
                            .build();
                    memberLoginAttemptRepository.save(updatedLoginAttempt);

                } else if (member.getStatus() == MemberStatus.LOCKED) {
                    // UnlockTime이 지나지 않았고, 상태가 여전히 LOCKED인 경우 예외 발생
                    throw new AccountStatusLockedException(USER_STATUS_LOCKED);
                }
            } else if (member.getStatus() == MemberStatus.LOCKED) {
                // 로그인 시도 기록이 없더라도 상태가 LOCKED인 경우 예외 발생
                throw new AccountStatusLockedException(USER_STATUS_LOCKED);
            }

            return new CustomUserDetails(member);
        } else {
            // 관리자 조회
            Admin admin = adminRepository.findByLoginId(username);
            if (admin == null) {
                throw new UsernameNotFoundException(NOT_FOUND_USER.getMessage());
            }

            // 로그인 시도 정보 확인
            AdminLoginAttempt loginAttempt = adminLoginAttemptRepository
                    .findByAdminAndLoginAttemptDetailsActiveYn(admin, "Y")
                    .orElse(null);

            if (loginAttempt != null) {
                if (loginAttempt.getLoginAttemptDetails().getUnlockTime() != null &&
                        loginAttempt.getLoginAttemptDetails().getUnlockTime().isBefore(LocalDateTime.now())) {

                    // UnlockTime이 지났으므로 계정 상태 업데이트 및 초기화
                    admin.activate(); // 계정 활성화
                    adminRepository.save(admin);

                    AdminLoginAttempt updatedLoginAttempt = AdminLoginAttempt.builder()
                            .no(loginAttempt.getNo())
                            .admin(loginAttempt.getAdmin())
                            .loginAttemptDetails(BaseLoginAttempt.builder()
                                    .failCount(0)
                                    .lastAttemptTime(LocalDateTime.now())
                                    .unlockTime(null)
                                    .activeYn("Y")
                                    .build())
                            .build();
                    adminLoginAttemptRepository.save(updatedLoginAttempt);

                } else if (admin.getStatus() == AdminStatus.LOCKED) {
                    // UnlockTime이 지나지 않았고, 상태가 여전히 LOCKED인 경우 예외 발생
                    throw new AccountStatusLockedException(USER_STATUS_LOCKED);
                }
            } else if (admin.getStatus() == AdminStatus.LOCKED) {
                // 로그인 시도 기록이 없더라도 상태가 LOCKED인 경우 예외 발생
                throw new AccountStatusLockedException(USER_STATUS_LOCKED);
            }


            return new CustomUserDetails(admin);
        }
    }

    // 이메일 형식 검사 메서드
    private boolean isEmail(String input) {
        return EMAIL_PATTERN.matcher(input).matches();
    }
}