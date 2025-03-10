package shoppingmall.ankim.domain.viewRolling.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
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
import shoppingmall.ankim.global.dummy.InitProduct;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@ActiveProfiles("prod")
@SpringBootTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
@Transactional
class ViewRollingServiceTest {
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

    @Autowired
    ViewRollingService viewRollingService;

    @Autowired
    EntityManager entityManager;

    @DisplayName("상품 등록 시 ViewRolling 데이터가 초기화된다.")
    @Test
    void initializeViewRolling() {
        // given
        Product product = ProductFactory.createProduct(entityManager);

        // when
        viewRollingService.initializeViewRolling(product.getCategory().getNo(), product.getNo());

        // then: 4개의 기간별 데이터(REALTIME, DAILY, WEEKLY, MONTHLY)가 생성되었는지 검증
        List<ViewRolling> viewRollings = viewRollingRepository.findByProduct_No(product.getNo());

        assertThat(viewRollings).hasSize(4); // REALTIME, DAILY, WEEKLY, MONTHLY 데이터가 생성되어야 함
        assertThat(viewRollings).extracting("period")
                .containsExactlyInAnyOrder(RollingPeriod.REALTIME, RollingPeriod.DAILY, RollingPeriod.WEEKLY, RollingPeriod.MONTHLY);
    }

    @DisplayName("상품의 실시간 조회수를 증가시킬 수 있다.")
    @Test
    void increaseRealTimeViewCount() {
        // given
        Product product = ProductFactory.createProduct(entityManager);
        viewRollingService.initializeViewRolling(product.getCategory().getNo(), product.getNo());

        int beforeViewCount = viewRollingRepository.findByProduct_No(product.getNo()).stream()
                .filter(v -> v.getPeriod() == RollingPeriod.REALTIME)
                .findFirst()
                .map(ViewRolling::getTotalViews)
                .orElseThrow(() -> new AssertionError("REALTIME 데이터가 존재하지 않음"));

        // when: 조회수 증가
        viewRollingService.increaseRealTimeViewCount(product.getNo());

        // then: REALTIME 조회수가 증가했는지 확인
        entityManager.flush();
        entityManager.clear();
        ViewRolling updatedRealTimeRolling = viewRollingRepository.findByProduct_No(product.getNo()).stream()
                .filter(v -> v.getPeriod() == RollingPeriod.REALTIME)
                .findFirst()
                .orElseThrow(() -> new AssertionError("REALTIME 데이터가 존재하지 않음"));

        assertThat(updatedRealTimeRolling.getTotalViews()).isEqualTo(beforeViewCount + 1);
    }

    @DisplayName("REALTIME 데이터를 DAILY로 집계할 수 있다.")
    @Test
    void rollupRealTimeToDaily() {
        // given
        Product product = ProductFactory.createProduct(entityManager);
        viewRollingService.initializeViewRolling(product.getCategory().getNo(), product.getNo());

        // REALTIME 조회수 증가
        for (int i = 0; i < 5; i++) {
            viewRollingService.increaseRealTimeViewCount(product.getNo());
        }

        // when: REALTIME → DAILY 누적
        viewRollingService.rollupRealTimeToDaily();

        // then: DAILY 데이터의 total_views가 증가했는지 확인
        entityManager.flush();
        entityManager.clear();
        ViewRolling dailyRolling = viewRollingRepository.findByProduct_No(product.getNo()).stream()
                .filter(v -> v.getPeriod() == RollingPeriod.DAILY)
                .findFirst()
                .orElseThrow(() -> new AssertionError("DAILY 데이터가 존재하지 않음"));

        assertThat(dailyRolling.getTotalViews()).isEqualTo(5);
    }

    @DisplayName("DAILY 데이터를 WEEKLY로 집계할 수 있다.")
    @Test
    void rollupDailyToWeekly() {
        // given
        Product product = ProductFactory.createProduct(entityManager);
        viewRollingService.initializeViewRolling(product.getCategory().getNo(), product.getNo());

        // DAILY 조회수 증가
        for (int i = 0; i < 20; i++) {
            viewRollingService.increaseRealTimeViewCount(product.getNo());
        }

        viewRollingService.rollupRealTimeToDaily();

        int beforeWeeklyViewCount = viewRollingRepository.findByProduct_No(product.getNo()).stream()
                .filter(v -> v.getPeriod() == RollingPeriod.WEEKLY)
                .findFirst()
                .map(ViewRolling::getTotalViews)
                .orElseThrow(() -> new AssertionError("WEEKLY 데이터가 존재하지 않음"));

        // when: DAILY → WEEKLY 누적
        viewRollingService.rollupDailyToWeekly();

        // then: WEEKLY 데이터의 total_views가 증가했는지 확인
        entityManager.flush();
        entityManager.clear();
        ViewRolling weeklyRolling = viewRollingRepository.findByProduct_No(product.getNo()).stream()
                .filter(v -> v.getPeriod() == RollingPeriod.WEEKLY)
                .findFirst()
                .orElseThrow(() -> new AssertionError("WEEKLY 데이터가 존재하지 않음"));

        assertThat(weeklyRolling.getTotalViews()).isEqualTo(beforeWeeklyViewCount + 20);
    }

    @DisplayName("WEEKLY 데이터를 MONTHLY로 집계할 수 있다.")
    @Test
    void rollupWeeklyToMonthly() {
        // given
        Product product = ProductFactory.createProduct(entityManager);
        viewRollingService.initializeViewRolling(product.getCategory().getNo(), product.getNo());

        // WEEKLY 조회수 증가
        for (int i = 0; i < 50; i++) {
            viewRollingService.increaseRealTimeViewCount(product.getNo());
        }

        viewRollingService.rollupRealTimeToDaily();
        viewRollingService.rollupDailyToWeekly();

        int beforeMonthlyViewCount = viewRollingRepository.findByProduct_No(product.getNo()).stream()
                .filter(v -> v.getPeriod() == RollingPeriod.MONTHLY)
                .findFirst()
                .map(ViewRolling::getTotalViews)
                .orElseThrow(() -> new AssertionError("MONTHLY 데이터가 존재하지 않음"));

        // when: WEEKLY → MONTHLY 누적
        viewRollingService.rollupWeeklyToMonthly();

        // then: MONTHLY 데이터의 total_views가 증가했는지 확인
        entityManager.flush();
        entityManager.clear();
        ViewRolling monthlyRolling = viewRollingRepository.findByProduct_No(product.getNo()).stream()
                .filter(v -> v.getPeriod() == RollingPeriod.MONTHLY)
                .findFirst()
                .orElseThrow(() -> new AssertionError("MONTHLY 데이터가 존재하지 않음"));

        assertThat(monthlyRolling.getTotalViews()).isEqualTo(beforeMonthlyViewCount + 50);
    }

    @DisplayName("REALTIME 데이터를 DAILY로 롤업한 후, 해당 조회수를 차감할 수 있다.")
    @Test
    void rollupRealTimeToDaily_And_Subtract() {
        // given
        Product product = ProductFactory.createProduct(entityManager);
        viewRollingService.initializeViewRolling(product.getCategory().getNo(), product.getNo());

        // 🔥 REALTIME 조회수 증가
        for (int i = 0; i < 10; i++) {
            viewRollingService.increaseRealTimeViewCount(product.getNo());
        }

        // when: REALTIME → DAILY 롤업
        viewRollingService.rollupRealTimeToDaily();
        viewRollingService.subtractRealTimeViews(); // 🔥 롤업된 만큼 REALTIME에서 차감

        // then: DAILY 증가 확인 + REALTIME 차감 확인
        entityManager.flush();
        entityManager.clear();

        ViewRolling dailyRolling = viewRollingRepository.findByProduct_No(product.getNo()).stream()
                .filter(v -> v.getPeriod() == RollingPeriod.DAILY)
                .findFirst()
                .orElseThrow(() -> new AssertionError("DAILY 데이터가 존재하지 않음"));

        ViewRolling realTimeRolling = viewRollingRepository.findByProduct_No(product.getNo()).stream()
                .filter(v -> v.getPeriod() == RollingPeriod.REALTIME)
                .findFirst()
                .orElseThrow(() -> new AssertionError("REALTIME 데이터가 존재하지 않음"));

        assertThat(dailyRolling.getTotalViews()).isEqualTo(10);
        assertThat(realTimeRolling.getTotalViews()).isEqualTo(0); // 🔥 차감된 후 REALTIME 조회수 0 확인
    }

    @DisplayName("DAILY 데이터를 WEEKLY로 롤업한 후, 해당 조회수를 차감할 수 있다.")
    @Test
    void rollupDailyToWeekly_And_Subtract() {
        // given
        Product product = ProductFactory.createProduct(entityManager);
        viewRollingService.initializeViewRolling(product.getCategory().getNo(), product.getNo());

        // 🔥 DAILY 데이터 증가
        for (int i = 0; i < 30; i++) {
            viewRollingService.increaseRealTimeViewCount(product.getNo());
        }
        viewRollingService.rollupRealTimeToDaily();

        // when: DAILY → WEEKLY 롤업
        viewRollingService.rollupDailyToWeekly();
        viewRollingService.subtractDailyViews(); // 🔥 롤업된 만큼 DAILY에서 차감

        // then: WEEKLY 증가 확인 + DAILY 차감 확인
        entityManager.flush();
        entityManager.clear();

        ViewRolling weeklyRolling = viewRollingRepository.findByProduct_No(product.getNo()).stream()
                .filter(v -> v.getPeriod() == RollingPeriod.WEEKLY)
                .findFirst()
                .orElseThrow(() -> new AssertionError("WEEKLY 데이터가 존재하지 않음"));

        ViewRolling dailyRolling = viewRollingRepository.findByProduct_No(product.getNo()).stream()
                .filter(v -> v.getPeriod() == RollingPeriod.DAILY)
                .findFirst()
                .orElseThrow(() -> new AssertionError("DAILY 데이터가 존재하지 않음"));

        assertThat(weeklyRolling.getTotalViews()).isEqualTo(30);
        assertThat(dailyRolling.getTotalViews()).isEqualTo(0); // 🔥 차감된 후 DAILY 조회수 0 확인
    }

    @DisplayName("WEEKLY 데이터를 MONTHLY로 롤업한 후, 해당 조회수를 차감할 수 있다.")
    @Test
    void rollupWeeklyToMonthly_And_Subtract() {
        // given
        Product product = ProductFactory.createProduct(entityManager);
        viewRollingService.initializeViewRolling(product.getCategory().getNo(), product.getNo());

        // 🔥 WEEKLY 데이터 증가
        for (int i = 0; i < 50; i++) {
            viewRollingService.increaseRealTimeViewCount(product.getNo());
        }
        viewRollingService.rollupRealTimeToDaily();
        viewRollingService.rollupDailyToWeekly();

        // when: WEEKLY → MONTHLY 롤업
        viewRollingService.rollupWeeklyToMonthly();
        viewRollingService.subtractWeeklyViews(); // 🔥 롤업된 만큼 WEEKLY에서 차감

        // then: MONTHLY 증가 확인 + WEEKLY 차감 확인
        entityManager.flush();
        entityManager.clear();

        ViewRolling monthlyRolling = viewRollingRepository.findByProduct_No(product.getNo()).stream()
                .filter(v -> v.getPeriod() == RollingPeriod.MONTHLY)
                .findFirst()
                .orElseThrow(() -> new AssertionError("MONTHLY 데이터가 존재하지 않음"));

        ViewRolling weeklyRolling = viewRollingRepository.findByProduct_No(product.getNo()).stream()
                .filter(v -> v.getPeriod() == RollingPeriod.WEEKLY)
                .findFirst()
                .orElseThrow(() -> new AssertionError("WEEKLY 데이터가 존재하지 않음"));

        assertThat(monthlyRolling.getTotalViews()).isEqualTo(50);
        assertThat(weeklyRolling.getTotalViews()).isEqualTo(0); // 🔥 차감된 후 WEEKLY 조회수 0 확인
    }


}