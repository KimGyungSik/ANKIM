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

@SpringBootTest
@Transactional
@TestPropertySource(properties = {
        "spring.sql.init.mode=never",
        "spring.profiles.active=prod" // "test" 프로파일 활성화
})
class KeywordSyncServiceTest {

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
    private RedisSearchLogService redisSearchLogService;

    @Autowired
    private KeywordSyncService keywordSyncService;

    @Autowired
    private SearchLogRepository searchLogRepository;

    @BeforeEach
    void cleanUp() {
        redisSearchLogService.clearKeywords();
        searchLogRepository.deleteAll();
    }

    @Test
    @DisplayName("Redis에 저장된 키워드와 점수를 MySQL로 동기화할 수 있다.")
    void syncRedisToMySQLTest() {
        // given (Redis에 키워드들 저장)
        redisSearchLogService.saveSearchKeyword("핸드백");
        redisSearchLogService.saveSearchKeyword("핸드백");
        redisSearchLogService.saveSearchKeyword("신발");

        // when (배치 동기화 실행)
        keywordSyncService.syncRedisToMySQL();

        // then
        List<SearchLog> logs = searchLogRepository.findAll();
        logs.forEach(log -> System.out.println(log.getKeyword() + " : " + log.getSearchCount()));

        assertThat(logs).hasSize(2);
        assertThat(logs).anyMatch(log ->
                log.getKeyword().equals("핸드백") && log.getSearchCount() == 2
        );
        assertThat(logs).anyMatch(log ->
                log.getKeyword().equals("신발") && log.getSearchCount() == 1
        );
    }
}