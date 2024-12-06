package shoppingmall.ankim.domain.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.admin.controller.request.AdminRegisterRequest;
import shoppingmall.ankim.domain.admin.entity.Admin;
import shoppingmall.ankim.domain.admin.exception.AdminRegistrationException;
import shoppingmall.ankim.domain.admin.repository.AdminRepository;
import shoppingmall.ankim.domain.admin.service.request.AdminIdValidServiceRequest;
import shoppingmall.ankim.domain.admin.service.request.AdminRegisterServiceRequest;
import shoppingmall.ankim.domain.security.handler.RedisHandler;

import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final RedisHandler redisHandler;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final String REDIS_KEY_PREFIX = "admin:validated:loginId:";
    private static final long LOGIN_ID_VALIDATION_TTL = 1800; // 30분

    @Override
    public void isLoginIdDuplicated(AdminIdValidServiceRequest request) {
        String loginId = request.getLoginId();
        if (adminRepository.existsByLoginId(loginId)) {
            throw new AdminRegistrationException(MEMBER_ID_DUPLICATE);
        }
        // Redis에 검증된 상태를 저장 (유효시간 30분으로 설정)
        try {
            redisHandler.save(REDIS_KEY_PREFIX + loginId, loginId, LOGIN_ID_VALIDATION_TTL);
            log.info("Redis에 검증된 로그인 아이디 상태 저장 성공: {}", loginId);
        } catch (Exception e) {
            log.error("Redis에 검증된 로그인 아이디 상태 저장 실패: {}", e.getMessage());
            throw new AdminRegistrationException(REDIS_ID_VALIDATION_SAVE_FAILED);
        }
    }

    /*
    * 1. Redis에서 검증된 아이디 확인
    * 2. 비밀번호 암호화
    * 3. 엔티티 생성(Admin, AdminAddress)
    * 4. DB에 데이터 저장
    * 5. redis에서 검증 아이디 값 삭제
    * */

    @Override
    public void register(AdminRegisterServiceRequest request) {
        String loginId = request.getLoginId();
        log.info("Starting admin registration process for loginId: {}", loginId);

        // Redis에서 검증된 아이디 확인
        String redisKey = REDIS_KEY_PREFIX + loginId;
        String validatedLoginId = (String) redisHandler.get(redisKey);
        if (validatedLoginId == null || !validatedLoginId.equals(loginId)) {
            throw new AdminRegistrationException(INVALID_LOGIN_ID);
        }

        // 비밀번호 암호화
        String encodePwd = bCryptPasswordEncoder.encode(request.getPwd());

        // Admin 엔티티 생성
        Admin admin = request.toAdminEntity(encodePwd);

        // DB에 저장
        adminRepository.save(admin);

        // Redis에서 검증된 로그인 아이디 제거
        redisHandler.delete(redisKey);
    }

}
