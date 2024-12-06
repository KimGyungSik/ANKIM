package shoppingmall.ankim.domain.security.handler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import shoppingmall.ankim.global.config.RedisConfig;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
@Import(RedisConfig.class)
class RedisHandlerTest {

    @Autowired
    private RedisHandler redisHandler;

    @Test
    void testSaveAndGet() {
        String TEST_KEY = "test-key";
        String TEST_VALUE = "test-value";
        long TTL_SECONDS = 60;

        // 기존 데이터 삭제
        redisHandler.delete(TEST_KEY);

        // 데이터 저장
        redisHandler.save(TEST_KEY, TEST_VALUE, TTL_SECONDS);

        // 데이터 조회
        Object result = redisHandler.get(TEST_KEY);

        // 검증
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(TEST_VALUE);
    }

    @Test
    void testDelete() {
        String TEST_KEY = "test-key";
        String TEST_VALUE = "test-value";
        long TTL_SECONDS = 60;

        // 기존 데이터 삭제
        redisHandler.delete(TEST_KEY);

        // 데이터 저장
        redisHandler.save(TEST_KEY, TEST_VALUE, TTL_SECONDS);

        // 데이터 삭제
        redisHandler.delete(TEST_KEY);

        // 삭제 확인
        Object result = redisHandler.get(TEST_KEY);
        assertThat(result).isNull();
    }

    @Test
    void testExists() {
        String TEST_KEY = "test-key";
        String TEST_VALUE = "test-value";
        long TTL_SECONDS = 60;

        // 기존 데이터 삭제
        redisHandler.delete(TEST_KEY);

        // 데이터 저장
        redisHandler.save(TEST_KEY, TEST_VALUE, TTL_SECONDS);

        // 데이터 존재 확인
        boolean exists = redisHandler.exists(TEST_KEY);
        assertThat(exists).isTrue();

        // 데이터 삭제 후 존재 확인
        redisHandler.delete(TEST_KEY);
        exists = redisHandler.exists(TEST_KEY);
        assertThat(exists).isFalse();
    }
}