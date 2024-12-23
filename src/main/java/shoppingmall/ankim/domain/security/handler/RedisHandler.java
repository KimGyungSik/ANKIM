package shoppingmall.ankim.domain.security.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisHandler {

    private final RedisTemplate<String, Object> redisTemplate;

    // 데이터 저장
    public void save(String key, Object value, long ttl) {
        try {
            String redisKey = "token:" + key;
            redisTemplate.opsForValue().set(redisKey, value, ttl, TimeUnit.SECONDS);
            log.info("Redis 데이터 저장 성공 : key={}, value={}, ttl={}초", redisKey, value, ttl);
        } catch (Exception e) {
            log.error("Redis 데이터 저장 실패 : {}", e.getMessage());
            throw new RuntimeException("Redis 데이터 저장 실패", e);
        }
    }

    // 데이터 조회
    public Object get(String key) {
        try {
            String redisKey = "token:" + key;
            return redisTemplate.opsForValue().get(redisKey);
        } catch (Exception e) {
            log.error("Redis 데이터 조회 실패: {}", e.getMessage());
            throw new RuntimeException("Redis 데이터 조회 실패", e);
        }
    }

    // 데이터 삭제
    public void delete(String key) {
        log.info("Redis 데이터 삭제 요청 들어오는지 확인 : {}", key);
        try {
            String redisKey = "token:" + key;
            log.info("Redis 데이터 삭제 요청: key={}", redisKey);
            redisTemplate.delete(redisKey);
            log.info("Redis 데이터 삭제 성공: key={}", redisKey);
        } catch (Exception e) {
            log.error("Redis 데이터 삭제 실패: {}", e.getMessage());
            throw new RuntimeException("Redis 데이터 삭제 실패", e);
        }
    }

    // 데이터 존재하는지 확인
    public boolean exists(String key) {
        try {
            String redisKey = "token:" + key;
            Boolean exists = redisTemplate.hasKey(redisKey);
            log.info("Redis Key 존재 여부 확인: key={}, exists={}", redisKey, exists);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("Redis Key 존재 여부 확인 실패: {}", e.getMessage());
            throw new RuntimeException("Redis Key 존재 여부 확인 실패", e);
        }
    }
}
