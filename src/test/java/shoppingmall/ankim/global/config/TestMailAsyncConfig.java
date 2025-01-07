package shoppingmall.ankim.global.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SyncTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@TestConfiguration
public class TestMailAsyncConfig {

    @PostConstruct
    public void init() {
        log.info("TestMailAsyncConfig 초기화");
    }

    @Bean(name = "mailTaskExecutor")
    public Executor taskExecutor() {
        log.info("메일 발송시 비동기 호출을 동기로 처리한다.");
        return new SyncTaskExecutor(); // 비동기 호출을 동기로 처리(@Async 비활성화)
    }
}
