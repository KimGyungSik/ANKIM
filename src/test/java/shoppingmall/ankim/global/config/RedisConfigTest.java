package shoppingmall.ankim.global.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
class RedisConfigTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    @DisplayName("Redis 연결 상태를 확인하고, 데이터 저장 및 조회가 정상적으로 작동하는지 테스트한다.")
    public void testRedisConnection() {
        // Redis에 데이터 저장
        String testKey = "testKey";
        String testValue = "testValue";
        redisTemplate.opsForValue().set(testKey, testValue);

        // Redis에서 데이터 가져오기
        String fetchedValue = (String) redisTemplate.opsForValue().get(testKey);

        // 데이터 검증
        assertThat(fetchedValue).isEqualTo(testValue);
    }

}