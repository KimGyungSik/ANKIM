package shoppingmall.ankim.domain.viewRolling.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.image.service.FileService;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.repository.ProductRepository;
import shoppingmall.ankim.domain.viewRolling.entity.RollingPeriod;
import shoppingmall.ankim.domain.viewRolling.entity.ViewRolling;
import shoppingmall.ankim.domain.viewRolling.repository.ViewRollingRepository;
import shoppingmall.ankim.factory.ProductFactory;
import shoppingmall.ankim.global.config.S3Config;
import shoppingmall.ankim.global.config.TestClockConfig;
import shoppingmall.ankim.global.config.TestClockHolder;
import shoppingmall.ankim.global.dummy.InitProduct;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("prod")
@Import(TestClockConfig.class)
@EnableScheduling
@SpringBootTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
@Transactional
class ViewRollingSchedulerTest {
    @MockBean
    InitProduct initProduct;

    @MockBean
    S3Service s3Service;

    @MockBean
    S3Config s3Config;

    @MockBean
    FileService fileService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ViewRollingRepository viewRollingRepository;

    @SpyBean
    ViewRollingScheduler viewRollingScheduler;

    @Autowired
    ViewRollingService viewRollingService;

    @Autowired
    EntityManager entityManager;


    @Autowired
    private TestClockHolder testClockHolder;

    private static Long productNo;

    @BeforeEach
    void setUp() {
        testClockHolder.changeTime(Instant.now().toEpochMilli()); // 현재시간을 기준으로 설정
        executeProductSetup();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void executeProductSetup() {
        Product product = ProductFactory.createProduct(entityManager);
        viewRollingService.initializeViewRolling(product.getCategory().getNo(), product.getNo());
        productNo = product.getNo();
    }

    @DisplayName("REALTIME 데이터를 DAILY로 롤업할 때 자정이 지나면 실행된다.")
    @Test
    void rollupDailyViews_AtMidnight() {
        // given
        for (int i = 0; i < 10; i++) {
            viewRollingService.increaseRealTimeViewCount(productNo);
        }
        entityManager.flush();
        entityManager.clear();

        // 자정 직전 설정
        testClockHolder.changeTime(Instant.now().plus(23, ChronoUnit.HOURS).plus(59, ChronoUnit.MINUTES).plus(59, ChronoUnit.SECONDS).toEpochMilli());

        // when: 자정을 지나 스케줄러 실행
        testClockHolder.changeTime(Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli());
        viewRollingScheduler.rollupDailyViews();

        entityManager.flush();
        entityManager.clear();

        // then: DAILY 데이터의 total_views가 증가했는지 검증
        ViewRolling dailyRolling = viewRollingRepository.findByProduct_No(productNo).stream()
                .filter(v -> v.getPeriod() == RollingPeriod.DAILY)
                .findFirst()
                .orElseThrow(() -> new AssertionError("DAILY 데이터가 존재하지 않음"));

        assertThat(dailyRolling.getTotalViews()).isEqualTo(10);
    }

    @DisplayName("DAILY 데이터를 WEEKLY로 롤업할 때 1주일 후 실행된다.")
    @Test
    void rollupWeeklyViews() {
        // given
        for (int i = 0; i < 20; i++) {
            viewRollingService.increaseRealTimeViewCount(productNo);
        }

        // 하루가 지나 DAILY 롤업 실행
        testClockHolder.changeTime(Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli());
        viewRollingScheduler.rollupDailyViews();

        // 1주일 후로 시간 이동
        testClockHolder.changeTime(Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli());

        // when: WEEKLY 롤업 실행
        viewRollingScheduler.rollupWeeklyViews();

        entityManager.flush();
        entityManager.clear();

        // then: WEEKLY 데이터의 total_views가 증가했는지 검증
        ViewRolling weeklyRolling = viewRollingRepository.findByProduct_No(productNo).stream()
                .filter(v -> v.getPeriod() == RollingPeriod.WEEKLY)
                .findFirst()
                .orElseThrow(() -> new AssertionError("WEEKLY 데이터가 존재하지 않음"));

        assertThat(weeklyRolling.getTotalViews()).isEqualTo(20);
    }

    @DisplayName("WEEKLY 데이터를 MONTHLY로 롤업할 때 1개월 후 실행된다.")
    @Test
    void rollupMonthlyViews() {
        // given
        for (int i = 0; i < 50; i++) {
            viewRollingService.increaseRealTimeViewCount(productNo);
        }

        // 1주일 후 이동하여 WEEKLY 롤업 실행
        testClockHolder.changeTime(Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli());
        viewRollingScheduler.rollupDailyViews();
        viewRollingScheduler.rollupWeeklyViews();

        // 1개월 후로 시간 이동
        testClockHolder.changeTime(Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli());

        // when: MONTHLY 롤업 실행
        viewRollingScheduler.rollupMonthlyViews();

        entityManager.flush();
        entityManager.clear();

        // then: MONTHLY 데이터의 total_views가 증가했는지 검증
        ViewRolling monthlyRolling = viewRollingRepository.findByProduct_No(productNo).stream()
                .filter(v -> v.getPeriod() == RollingPeriod.MONTHLY)
                .findFirst()
                .orElseThrow(() -> new AssertionError("MONTHLY 데이터가 존재하지 않음"));

        assertThat(monthlyRolling.getTotalViews()).isEqualTo(50);
    }
}
