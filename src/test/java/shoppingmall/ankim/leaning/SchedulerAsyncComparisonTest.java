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
import shoppingmall.ankim.leaning.subject.*;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("prod")
@Import({SchedulerConfig.class, TestSchedulersConfig.class})
@Transactional
@TestPropertySource(properties = "spring.sql.init.mode=never")
class SchedulerAsyncComparisonTest {
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

    @MockBean
    private SchedulerA schedulerA;

    @MockBean
    private SchedulerB schedulerB;

    @MockBean
    private SyncPollingScheduler syncPollingScheduler;


    @Test
    void waitAndObserveSchedulerBehavior() throws InterruptedException {
        System.out.println("🚀 테스트 시작 - 로그 확인을 위해 30초 대기...");
        Thread.sleep(30000);
        System.out.println("✅ 테스트 완료 - 콘솔에서 실행 타이밍 확인해보세요!");
    }
}
