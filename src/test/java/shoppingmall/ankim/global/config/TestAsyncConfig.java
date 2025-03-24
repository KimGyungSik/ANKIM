package shoppingmall.ankim.global.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SyncTaskExecutor;

import java.util.concurrent.Executor;

@TestConfiguration
public class TestAsyncConfig {
    @Bean
    public Executor taskExecutor() {
        return new SyncTaskExecutor(); // 🚀 `@Async` 메서드를 동기적으로 실행
    }
}

