package shoppingmall.ankim.domain.viewRolling.repository.query;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.product.dto.ProductListResponse;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.viewRolling.entity.RollingPeriod;
import shoppingmall.ankim.domain.viewRolling.entity.ViewRolling;
import shoppingmall.ankim.domain.viewRolling.repository.ViewRollingRepository;
import shoppingmall.ankim.factory.ProductFactory;
import shoppingmall.ankim.global.config.QuerydslConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
@Import(QuerydslConfig.class) // QuerydslConfig를 테스트에 추가
class ViewRollingQueryRepositoryTest {
    @MockBean
    private S3Service s3Service;

    @Autowired
    EntityManager em;

    @Autowired
    ViewRollingRepository viewRollingRepository;

    private List<Product> products;

    @BeforeEach
    void setup() {
        products = ProductFactory.createProducts(em);
        em.flush();
        em.clear();
    }

    @DisplayName("실시간 조회순으로 상품을 정렬하여 50개 가져올 수 있다.")
    @Test
    @Rollback(value = false)
    void getViewRollingProductsWithRealTime() {
        Page<ProductListResponse> productList = viewRollingRepository.getViewRollingProducts(products.get(0).getCategory().getNo(), RollingPeriod.REALTIME, PageRequest.of(0, 50));

        assertThat(productList).isNotEmpty();
        assertThat(productList.getContent().size()).isLessThanOrEqualTo(50);

        for (int i = 1; i < productList.getContent().size(); i++) {
            assertThat(productList.getContent().get(i - 1).getViewCnt()).isGreaterThanOrEqualTo(productList.getContent().get(i).getViewCnt());
        }
    }

    @DisplayName("일간 조회순으로 상품을 정렬하여 50개 가져올 수 있다.")
    @Test
    void getViewRollingProductsWithDaily() {
        Page<ProductListResponse> productList = viewRollingRepository.getViewRollingProducts(products.get(0).getCategory().getNo(), RollingPeriod.DAILY, PageRequest.of(0, 50));

        assertThat(productList).isNotEmpty();
        assertThat(productList.getContent().size()).isLessThanOrEqualTo(50);

        for (int i = 1; i < productList.getContent().size(); i++) {
            assertThat(productList.getContent().get(i - 1).getViewCnt()).isGreaterThanOrEqualTo(productList.getContent().get(i).getViewCnt());
        }
    }

    @DisplayName("주간 조회순으로 상품을 정렬하여 50개 가져올 수 있다.")
    @Test
    void getViewRollingProductsWithWeekly() {
        Page<ProductListResponse> productList = viewRollingRepository.getViewRollingProducts(products.get(0).getCategory().getNo(), RollingPeriod.WEEKLY, PageRequest.of(0, 50));

        assertThat(productList).isNotEmpty();
        assertThat(productList.getContent().size()).isLessThanOrEqualTo(50);

        for (int i = 1; i < productList.getContent().size(); i++) {
            assertThat(productList.getContent().get(i - 1).getViewCnt()).isGreaterThanOrEqualTo(productList.getContent().get(i).getViewCnt());
        }
    }

    @DisplayName("월간 조회순으로 상품을 정렬하여 50개 가져올 수 있다.")
    @Test
    void getViewRollingProductsWithMonthly() {
        Page<ProductListResponse> productList = viewRollingRepository.getViewRollingProducts(products.get(0).getCategory().getNo(), RollingPeriod.MONTHLY, PageRequest.of(0, 50));

        assertThat(productList).isNotEmpty();
        assertThat(productList.getContent().size()).isLessThanOrEqualTo(50);

        for (int i = 1; i < productList.getContent().size(); i++) {
            assertThat(productList.getContent().get(i - 1).getViewCnt()).isGreaterThanOrEqualTo(productList.getContent().get(i).getViewCnt());
        }
    }
}
