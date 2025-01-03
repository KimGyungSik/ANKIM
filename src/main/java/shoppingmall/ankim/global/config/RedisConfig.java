package shoppingmall.ankim.global.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@EnableRedisRepositories
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    // Redis 연결 주소의 접두사로 사용할 상수 선언
    private static final String REDISSON_HOST_PREFIX = "redis://";

    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        config.useSingleServer().setAddress(REDISSON_HOST_PREFIX + host + ":" + port);

        return Redisson.create(config);
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // Redis 연결
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        // Key-Value 형태 직렬화
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        // Hash Key-Value 형태 직렬화
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        // 기본 직렬화 수행
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());

        return redisTemplate;
    }

/*
    // 리스트 연산 수행
    public ListOperations<String, Object> getListOperations() {
        return this.redisTemplate().opsForList();
    }

    // 단일 데이터 연산 수행
    public ValueOperations<String, Object> getValueOperations() {
        return this.redisTemplate().opsForValue();
    }

    // CUD 처리 및 예외처리 수행
    public int executeOperation(Runnable operation) {
        try {
            operation.run();
            return 1;
        } catch (Exception e) {
            log.info("Redis 작업 오류 발생 : " + e.getMessage());
            return 0;
        }
    }
*/



}
