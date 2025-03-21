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
import shoppingmall.ankim.leaning.subject.TestConflictScheduler;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("prod")
@Import({SchedulerConfig.class,TestSchedulersConfig.class})
@Transactional
@TestPropertySource(properties = "spring.sql.init.mode=never")
public class ShedLockConflictLeaningTest {
    @MockBean
    private S3Service s3Service;

    @MockBean
    private InitProduct initProduct;

    @MockBean
    private S3Config s3Config;
    @DisplayName("같은_lock_name을_사용하는_두_스케줄러는_동시에_실행되지_않는다")
    @Test
    void test() throws Exception {
        // 잠시 기다리며 스케줄러가 동작하도록 유도 (2.5초 대기)
        Thread.sleep(2500);

        // 2번 중복 실행을 시도했지만 실제 실행된 건 1번이어야 함
        int count = TestConflictScheduler.executedCount.get();
        System.out.println("⚠️ 실제 실행된 횟수: " + count);

        assertThat(count)
                .as("lock name이 같으므로 동시에 실행되면 안됨")
                .isEqualTo(1);
    }
}
