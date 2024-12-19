package shoppingmall.ankim.domain.email.handler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import shoppingmall.ankim.domain.email.exception.MailVerificationInProgressException;

import java.util.concurrent.TimeUnit;

import static shoppingmall.ankim.global.exception.ErrorCode.TOO_MANY_MAIL_CODE_REQUESTS;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailVerificationHandler {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String KEY_PREFIX = "email:verification:";
    private static final String REQUEST_COUNT_KEY_PREFIX = KEY_PREFIX + "count:";
    private static final long TTL = 300; // 인증코드 유효시간 5분

    private static final int MAX_REQUESTS = 3; // 최대 이메일 요청 허용 수
    private static final long BLOCK_TTL = 300; // 제한 시간 5분


    private HashOperations<String, String, Object> hashOps;

    @PostConstruct
    public void init() {
        hashOps = redisTemplate.opsForHash();
    }

    // 인증 코드 저장
    public void saveVerificationCode(String loginId, String code) {
        String key = KEY_PREFIX + loginId; // 인증 데이터 키
        String requestCountKey = REQUEST_COUNT_KEY_PREFIX + loginId; // 요청 횟수 키

        // 요청 횟수 확인 및 증가
        Long requestCount = redisTemplate.opsForValue().increment(requestCountKey, 1);

        if (requestCount == MAX_REQUESTS) {
            // 최대 요청 시 TTL 설정
            redisTemplate.expire(requestCountKey, BLOCK_TTL, TimeUnit.SECONDS);
        }

        if (requestCount > MAX_REQUESTS) {
            // TTL이 이미 설정되어 있는지 확인 후, 설정되지 않았다면 TTL 설정
            Long currentTtl = redisTemplate.getExpire(requestCountKey, TimeUnit.SECONDS);
            if (currentTtl == -1) { // -1이면 TTL이 설정되지 않은 상태
                redisTemplate.expire(requestCountKey, BLOCK_TTL, TimeUnit.SECONDS);
            }
            throw new MailVerificationInProgressException(TOO_MANY_MAIL_CODE_REQUESTS);
        }

        // 새로운 인증 코드 저장
        hashOps.put(key, "code", code);
        hashOps.put(key, "verified", "false");
        hashOps.put(key, "failCount", "0");
        redisTemplate.expire(key, TTL, TimeUnit.SECONDS);
    }

    // 인증 코드 가져오기
    public String getVerificationCode(String loginId) {
        String key = KEY_PREFIX + loginId;
        return (String) hashOps.get(key, "code");
    }

    // 실패 횟수 증가
    public int incrementFailCount(String loginId) {
        String key = KEY_PREFIX + loginId;
        Long failCount = hashOps.increment(key, "failCount", 1);
        return failCount.intValue();
    }

    // 실패 횟수 초기화
    public void resetFailCount(String loginId) {
        String key = KEY_PREFIX + loginId;
        hashOps.put(key, "failCount", "0");
    }

    // 인증 여부 확인
    public boolean isVerified(String loginId) {
        String key = KEY_PREFIX + loginId;
        return "true".equals(hashOps.get(key, "verified"));
    }

    // 인증 성공 처리
    public void setVerified(String loginId) {
        String key = KEY_PREFIX + loginId;
        hashOps.put(key, "verified", "true");
    }

    // 회원가입 성공 시 데이터 삭제
    public void deleteVerificationData(String loginId) {
        String key = KEY_PREFIX + loginId;
        String requestCountKey = REQUEST_COUNT_KEY_PREFIX + loginId;
        redisTemplate.delete(key);
        redisTemplate.delete(requestCountKey);
    }

}
