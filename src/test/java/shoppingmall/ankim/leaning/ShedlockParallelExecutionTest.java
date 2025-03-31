package shoppingmall.ankim.leaning;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
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
import shoppingmall.ankim.leaning.subject.SchedulerA;
import shoppingmall.ankim.leaning.subject.SchedulerB;
import shoppingmall.ankim.leaning.subject.TestAsyncScheduler;
import shoppingmall.ankim.leaning.subject.TestConflictScheduler;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("prod")
@Import({SchedulerConfig.class, TestSchedulersConfig.class})
@Transactional
@TestPropertySource(properties = "spring.sql.init.mode=never")
class ShedlockParallelExecutionTest {
    @MockBean
    private S3Service s3Service;

    @MockBean
    private InitProduct initProduct;

    @MockBean
    private S3Config s3Config;

    @MockBean
    private TestAsyncScheduler testAsyncScheduler;

    @MockBean
    private TestConflictScheduler testConflictScheduler;


    @BeforeEach
    void resetSchedulers() {
        SchedulerA.reset();
        SchedulerB.reset();
    }

    @Test
    @DisplayName("Bean이 다르고 lock name도 다르면 병렬 실행된다")
    void differentBeansAndLockNames_shouldExecuteInParallel() throws Exception {
        boolean aCompleted = SchedulerA.latch.await(5, TimeUnit.SECONDS);
        boolean bCompleted = SchedulerB.latch.await(5, TimeUnit.SECONDS);

        assertThat(aCompleted).isTrue();
        assertThat(bCompleted).isTrue();

        long diff = Math.abs(SchedulerA.executedAt - SchedulerB.executedAt);
        System.out.println("✅ A 실행 시각: " + SchedulerA.executedAt);
        System.out.println("✅ B 실행 시각: " + SchedulerB.executedAt);
        System.out.println("🕒 실행 시각 차이(ms): " + diff);

        // 실행 시점 차이가 1초 이하라면 동시에 실행된 것으로 판단
        assertThat(diff).isLessThanOrEqualTo(1000);
    }
}
