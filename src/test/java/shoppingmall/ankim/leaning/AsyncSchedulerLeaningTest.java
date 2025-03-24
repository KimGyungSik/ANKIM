package shoppingmall.ankim.leaning;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.global.config.S3Config;
import shoppingmall.ankim.global.config.SchedulerConfig;
import shoppingmall.ankim.global.config.TestSchedulersConfig;
import shoppingmall.ankim.global.dummy.InitProduct;
import shoppingmall.ankim.leaning.subject.TestAsyncScheduler;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("prod")
@Import({SchedulerConfig.class, TestSchedulersConfig.class})
@Transactional
@TestPropertySource(properties = "spring.sql.init.mode=never")
public class AsyncSchedulerLeaningTest {
    @MockBean
    private S3Service s3Service;

    @MockBean
    private InitProduct initProduct;

    @MockBean
    private S3Config s3Config;
    @DisplayName("async_스케줄러가_각각_다른_스레드에서_실행되는지_확인")
    @Test
    void test() throws Exception {
        // 최대 5초 동안 2개의 스케줄러 실행 기다림
        boolean completed = TestAsyncScheduler.latch.await(5, TimeUnit.SECONDS);

        assertThat(completed)
                .as("2개의 비동기 스케줄러가 정상적으로 실행되었는지 확인")
                .isTrue();
    }
}
