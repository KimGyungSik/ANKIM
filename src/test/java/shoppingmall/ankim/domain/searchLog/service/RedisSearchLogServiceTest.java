package shoppingmall.ankim.domain.searchLog.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.email.service.MailService;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.searchLog.entity.SearchLog;
import shoppingmall.ankim.domain.searchLog.repository.SearchLogRepository;
import shoppingmall.ankim.global.config.S3Config;
import shoppingmall.ankim.global.config.mail.MailAsyncConfig;
import shoppingmall.ankim.global.config.mail.MailConfig;
import shoppingmall.ankim.global.dummy.InitProduct;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(properties = {
        "spring.sql.init.mode=never",
        "spring.profiles.active=prod" // "test" 프로파일 활성화
})
class RedisSearchLogServiceTest {

    @MockBean
    private S3Service s3Service;

    @MockBean
    private MailConfig mailConfig;

    @MockBean
    private MailService mailService;

    @MockBean
    private MailAsyncConfig mailAsyncConfig;

    @MockBean
    private InitProduct initProduct;

    @MockBean
    private S3Config s3Config;

    @Autowired
    RedisSearchLogService redisSearchLogService;

    @BeforeEach
    void setup() {
        redisSearchLogService.clearKeywords(); // 매 테스트 전 초기화
    }

    @Test
    @DisplayName("검색 키워드를 Redis에 저장하면 점수가 1로 저장된다.")
    void saveSearchKeyword() {
        // given
        String keyword = "핸드백";

        // when
        redisSearchLogService.saveSearchKeyword(keyword);

        // then
        Double score = redisSearchLogService.getKeywordScore(keyword);
        assertThat(score).isEqualTo(1.0);
    }

    @Test
    @DisplayName("동일 키워드를 여러 번 저장하면 점수가 누적된다.")
    void updateSearchKeyword() {
        // given
        String keyword = "핸드백";

        // when
        redisSearchLogService.saveSearchKeyword(keyword);
        redisSearchLogService.saveSearchKeyword(keyword);
        redisSearchLogService.saveSearchKeyword(keyword);

        // then
        Double score = redisSearchLogService.getKeywordScore(keyword);
        assertThat(score).isEqualTo(3.0);
    }

    @Test
    @DisplayName("인기 검색어 상위 10개를 점수 순으로 가져온다.")
    void getPopularKeywords() {
        // given
        redisSearchLogService.saveSearchKeyword("핸드백"); // 3
        redisSearchLogService.saveSearchKeyword("핸드백");
        redisSearchLogService.saveSearchKeyword("핸드백");

        redisSearchLogService.saveSearchKeyword("모자"); // 2
        redisSearchLogService.saveSearchKeyword("모자");

        redisSearchLogService.saveSearchKeyword("신발"); // 1

        // when
        List<String> result = redisSearchLogService.getPopularKeywords(10);

        // then
        assertThat(result).containsExactly("핸드백", "모자", "신발");
    }

    @Test
    @DisplayName("10개의 쓰레드가 동시에 같은 키워드를 저장할 경우 점수가 정확히 증가한다.")
    void concurrentSaveTest() throws InterruptedException {
        // given
        String keyword = "핸드백";
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                try {
                    redisSearchLogService.saveSearchKeyword(keyword);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        Double score = redisSearchLogService.getKeywordScore(keyword);
        assertThat(score).isEqualTo(10.0);
    }
}