package shoppingmall.ankim.domain.login.service;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.admin.entity.Admin;
import shoppingmall.ankim.domain.admin.entity.AdminStatus;
import shoppingmall.ankim.domain.admin.repository.AdminRepository;
import shoppingmall.ankim.domain.login.entity.BaseLoginAttempt;
import shoppingmall.ankim.domain.login.entity.admin.loginHistory.AdminLoginAttempt;
import shoppingmall.ankim.domain.login.entity.admin.loginHistory.AdminLoginHistory;
import shoppingmall.ankim.domain.login.entity.member.loginHistory.MemberLoginAttempt;
import shoppingmall.ankim.domain.login.entity.member.loginHistory.MemberLoginHistory;
import shoppingmall.ankim.domain.login.exception.AdminLoginFailedException;
import shoppingmall.ankim.domain.login.exception.MemberLoginFailedException;
import shoppingmall.ankim.domain.login.repository.admin.AdminLoginAttemptRepository;
import shoppingmall.ankim.domain.login.repository.admin.AdminLoginHistoryRepository;
import shoppingmall.ankim.domain.login.repository.member.MemberLoginAttemptRepository;
import shoppingmall.ankim.domain.login.repository.member.MemberLoginHistoryRepository;
import shoppingmall.ankim.domain.login.service.request.LoginServiceRequest;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.member.repository.MemberRepository;
import shoppingmall.ankim.domain.memberHistory.entity.MemberHistory;
import shoppingmall.ankim.domain.memberHistory.repository.MemberHistoryRepository;
import shoppingmall.ankim.domain.security.dto.CustomUserDetails;
import shoppingmall.ankim.domain.security.handler.RedisHandler;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static shoppingmall.ankim.domain.memberHistory.handler.MemberHistoryHandler.handleStatusChange;
import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final EntityManager em;

    private final MemberRepository memberRepository;
    private final MemberLoginAttemptRepository memberLoginAttemptRepository;
    private final MemberLoginHistoryRepository memberLoginHistoryRepository;
    private final MemberHistoryRepository memberHistoryRepository;

    private final AdminRepository adminRepository;
    private final AdminLoginAttemptRepository adminLoginAttemptRepository;
    private final AdminLoginHistoryRepository adminLoginHistoryRepository;

//    private final LoginFailureHandler loginFailureHandler;

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RedisHandler redisHandler;

    @Value("${login.attempt.max}")
    private int MAX_LOGIN_ATTEMPTS; // 최대 로그인 시도 횟수

    @Value("${login.lock.time}")
    private int LOCK_TIME_MINUTES; // 잠금 시간 (분)

    @Value("${jwt.refresh.token.expire.time}")
    private long REFRESH_TOKEN_EXPIRE_TIME; // 토큰 만료시간(자동 로그인 X)

    @Value("${jwt.refresh.token.remember.time}")
    private long REFRESH_TOKEN_REMEBER_EXPIRE_TIME; // 토큰 만료시간(자동 로그인 O)

    @Override
    public Map<String, Object> memberLogin(LoginServiceRequest loginServiceRequest, HttpServletRequest request) {
        // 사용자 조회 (status에 따라서 상태를 반환해줘야 됨)
        Optional<Member> findMember = memberRepository.findByLoginIdExcludingWithdrawn(loginServiceRequest.getLoginId());

        // 사용자가 없는 경우
        if(findMember.isEmpty()) {
            throw new MemberLoginFailedException(USER_NOT_FOUND);
        }

        Member member = findMember.get();

        // 로그인 시도 기록 가져오기
        MemberLoginAttempt loginAttempt = memberLoginAttemptRepository
                .findByMemberAndLoginAttemptDetailsActiveYn(member, "Y")
                .orElseGet(() -> createNewLoginAttempt(member));

        // 회원 계정이 잠김 상태인 경우
        if (member.getStatus() == MemberStatus.LOCKED && !loginAttempt.isUnlockTimePassed()) {
            throw new MemberLoginFailedException(USER_STATUS_LOCKED);
        }

        // 비밀번호 검증
        // 사용자 세부 정보 생성 (CustomUserDetails)
        // Jwt 토큰 생성
        String username = loginServiceRequest.getLoginId();
        String password = loginServiceRequest.getPwd();

        try {
            // Spring Security를 통해 인증 시도
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // 인증 성공 시 실패 기록 초기화
            loginAttempt.resetFailCount();
            memberLoginAttemptRepository.save(loginAttempt);

            // 사용자(클라이언트)의 ip주소 가져오기
            String ipAddress = getClientIp(request);
            log.info("사용자 ip 주소 : {}", ipAddress);

            // 로그인 성공 이력 저장
            MemberLoginHistory hisotry = MemberLoginHistory.recordLoginHistory(member, ipAddress, loginServiceRequest);
            memberLoginHistoryRepository.save(hisotry);

            // JWT 생성 및 반환 ( 기존 : LoginFilter의 successfulAuthentication )
            // username, role을 가지고 있음
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            // 토큰 생성 및 refresh token 저장(자동로그인 체크여부에 따라서 refresh 토큰 만료 시간이 달라짐)
            return successfulAuthentication(userDetails, loginServiceRequest.getAutoLogin());

        } catch (BadCredentialsException | UnknownHostException ex) {
            log.error("로그인 실패");
            // 실패 시 처리
            handleLoginFailure(member, loginAttempt);
//            loginFailureHandler.handleMemberLoginFailure(member, loginAttempt); // 별도 서비스 호출
            throw new MemberLoginFailedException(INVALID_CREDENTIALS);
        }
    }

    @Override
    public Map<String, Object> adminLogin(LoginServiceRequest loginServiceRequest, HttpServletRequest request) {
        // 퇴사하지 않은 사용자 조회
        Admin admin = adminRepository.findByLoginIdExcludingResigned(loginServiceRequest.getLoginId());

        // 사용자가 없는 경우
        if(admin == null) {
            throw new AdminLoginFailedException(USER_NOT_FOUND);
        }

        // 로그인 시도 기록 가져오기
        AdminLoginAttempt loginAttempt = adminLoginAttemptRepository
                .findByAdminAndLoginAttemptDetailsActiveYn(admin, "Y")
                .orElseGet(() -> createNewLoginAttempt(admin));

        // 관리자 계정이 잠김 상태인 경우
        if (admin.getStatus() == AdminStatus.LOCKED && !loginAttempt.isUnlockTimePassed()) {
            throw new AdminLoginFailedException(USER_STATUS_LOCKED);
        }

        // 비밀번호 검증
        // 사용자 세부 정보 생성 (CustomUserDetails)
        // Jwt 토큰 생성
        String username = loginServiceRequest.getLoginId();
        String password = loginServiceRequest.getPwd();

        try {
            // Spring Security를 통해 인증 시도
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // 인증 성공 시 실패 기록 초기화
            loginAttempt.resetFailCount();
            adminLoginAttemptRepository.save(loginAttempt);

            // 사용자(클라이언트)의 ip주소 가져오기
            String ipAddress = getClientIp(request);

            // 로그인 성공 이력 저장
            AdminLoginHistory hisotry = AdminLoginHistory.recordLoginHistory(admin, ipAddress, loginServiceRequest);
            adminLoginHistoryRepository.save(hisotry);

            // JWT 생성 및 반환 ( 기존 : LoginFilter의 successfulAuthentication )
            // username, role을 가지고 있음
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            // 토큰 생성 및 refresh token 저장
            return successfulAuthentication(userDetails, loginServiceRequest.getAutoLogin());

        } catch (BadCredentialsException | UnknownHostException ex) {
            // 실패 시 처리
            handleLoginFailure(admin, loginAttempt);
            throw new AdminLoginFailedException(INVALID_CREDENTIALS);
        }
    }

    private void handleLoginFailure(Member member, MemberLoginAttempt loginAttempt) {
        log.error("로그인 실패 hadleLoginFailure");
        try {
            int failCount = loginAttempt.increaseFailCount();
            log.info("로그인 실패 횟수 : {}", failCount);
            memberLoginAttemptRepository.save(loginAttempt);

            if (failCount >= MAX_LOGIN_ATTEMPTS) {
                MemberHistory history = handleStatusChange(member, MemberStatus.LOCKED);
                memberHistoryRepository.save(history);

                member.lock();
                loginAttempt.setLockTime(LOCK_TIME_MINUTES);
                memberRepository.save(member);
            }
        } catch (Exception ex) {
            log.error("로그인 실패 처리 중 예외 발생: {}", ex.getMessage(), ex);
        }
    }

    private void handleLoginFailure(Admin admin, AdminLoginAttempt loginAttempt) {
        log.info("loginAttemp : {}", loginAttempt.getLoginAttemptDetails().getFailCount());
        // 실패 횟수 증가
        int failcount = loginAttempt.increaseFailCount();
        log.info("로그인 실패 횟수 증가 : {}", loginAttempt.getLoginAttemptDetails().getFailCount());
        adminLoginAttemptRepository.save(loginAttempt);

        // 최대 실패 횟수 초과 시 계정 잠금
        if (failcount >= MAX_LOGIN_ATTEMPTS) {
            admin.lock();
            loginAttempt.setLockTime(LOCK_TIME_MINUTES);
            adminRepository.save(admin);
        }
    }

    // 새로운 로그인 시도 객체 생성
    private MemberLoginAttempt createNewLoginAttempt(Member member) {
        MemberLoginAttempt loginAttempt = MemberLoginAttempt.builder()
                .member(member)
                .loginAttemptDetails(createBaseLoginAttempt())
                .build();
        return memberLoginAttemptRepository.save(loginAttempt);
    }

    private AdminLoginAttempt createNewLoginAttempt(Admin admin) {
        AdminLoginAttempt loginAttempt = AdminLoginAttempt.builder()
                .admin(admin)
                .loginAttemptDetails(createBaseLoginAttempt())
                .build();
        return adminLoginAttemptRepository.save(loginAttempt);
    }

    private BaseLoginAttempt createBaseLoginAttempt() {
        return BaseLoginAttempt.builder()
                .failCount(0)
                .lastAttemptTime(LocalDateTime.now())
                .unlockTime(null)
                .activeYn("Y")
                .build();
    }

    // 접속 ip 조회
    public static String getClientIp(HttpServletRequest request) throws UnknownHostException {
        // 요청 헤더에서 "X-Forwarded-For" 값 확인 (프록시 서버 또는 로드 밸런서를 통해 전달된 클라이언트의 실제 IP 주소를 식별)
        String ip = request.getHeader("X-Forwarded-For");

        // IP 값이 비어있거나 "unknown"일 경우, 다른 헤더를 차례대로 확인
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP"); // Apache HTTP 서버 또는 프록시 서버에서 전달된 클라이언트 IP
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP"); // WebLogic 서버에서 사용되는 헤더
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP"); // 일부 프록시에서 클라이언트 IP를 지정하는 헤더
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR"); // 또 다른 프록시 IP 헤더
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP"); // 일부 프록시 또는 CDN에서 클라이언트 IP를 전달하는 헤더
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-RealIP"); // 비슷한 역할의 헤더 (오타 또는 다양한 구현에서 사용)
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("REMOTE_ADDR"); // 클라이언트의 원격 IP를 포함하는 헤더
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr(); // HttpServletRequest에서 제공하는 기본 클라이언트 IP 값
        }

        // 로컬 IP인 경우 ("127.0.0.1" 또는 "0:0:0:0:0:0:0:1") 호스트 이름 및 호스트 주소로 대체
        if(ip.equals("0:0:0:0:0:0:0:1") || ip.equals("127.0.0.1"))
        {
            InetAddress address = InetAddress.getLocalHost(); // 로컬 호스트 정보 가져오기
            ip = address.getHostName() + "/" + address.getHostAddress(); // 호스트 이름과 IP 주소 결합
        }

        // local인 경우
        if(ip.equals("0:0:0:0:0:0:0:1") || ip.equals("127.0.0.1"))
        {
            InetAddress address = InetAddress.getLocalHost();
            ip = address.getHostName() + "/" + address.getHostAddress();
        }

        return ip;
    }

    // 로그인 성공
    private Map<String, Object> successfulAuthentication(CustomUserDetails userDetails, String autoLogin) {
        String access = jwtTokenProvider.generateAccessToken(userDetails, "access");

        long expireTime = autoLogin != null && autoLogin.equals("rememberMe") ? REFRESH_TOKEN_REMEBER_EXPIRE_TIME : REFRESH_TOKEN_EXPIRE_TIME;

        String refresh = jwtTokenProvider.generateRefreshToken(userDetails, "refresh", expireTime);

        Map<String, Object> token = new HashMap<>();
        token.put("access", access);
        token.put("refresh", refresh);
        token.put("expireTime", expireTime);

        addRefreshToken(access, refresh, autoLogin);

        return token;
    }

    // refresh token 저장
    private void addRefreshToken(String access, String refresh, String autoLogin) {
        if(autoLogin != null && autoLogin.equals("rememberMe")) {
            redisHandler.save(access, refresh, REFRESH_TOKEN_REMEBER_EXPIRE_TIME);
        } else {
            redisHandler.save(access, refresh, REFRESH_TOKEN_EXPIRE_TIME);
        }
    }
}
