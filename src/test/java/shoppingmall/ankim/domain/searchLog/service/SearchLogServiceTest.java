package shoppingmall.ankim.domain.searchLog.service;

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
class SearchLogServiceTest {

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
    SearchLogService searchLogService;
    @Autowired
    private SearchLogRepository searchLogRepository;

    @DisplayName("검색 키워드를 저장할 수 있다.")
    @Test
    void saveSearchKeyword() {
        // given
        String keyword = "핸드백";

        // when
        searchLogService.saveSearchKeywordWithCurrency(keyword);

        // then
        List<SearchLog> logs = searchLogRepository.findAll();
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getKeyword()).isEqualTo(keyword);
        assertThat(logs.get(0).getSearchCount()).isEqualTo(1);
    }

    @DisplayName("검색 키워드가 존재한다면 검색 횟수를 1 증가시킬 수 있다.")
    @Test
    void updateSearchKeyword() {
        // given
        String keyword = "핸드백";
        searchLogService.saveSearchKeywordWithCurrency(keyword); // 첫 번째 저장
        searchLogService.saveSearchKeywordWithCurrency(keyword); // 두 번째 저장 (횟수 증가)

        // when
        SearchLog log = searchLogRepository.findAll().get(0);

        // then
        assertThat(log.getSearchCount()).isEqualTo(2);
    }

    @DisplayName("인기 검색어를 1등부터 10등까지 가져올 수 있다.")
    @Test
    void getPopularKeywords() {
        // given (각 검색어의 검색 횟수를 다르게 설정)
        searchLogService.saveSearchKeywordWithCurrency("핸드백"); // 4번 검색
        searchLogService.saveSearchKeywordWithCurrency("핸드백");
        searchLogService.saveSearchKeywordWithCurrency("핸드백");
        searchLogService.saveSearchKeywordWithCurrency("핸드백");

        searchLogService.saveSearchKeywordWithCurrency("신발"); // 3번 검색
        searchLogService.saveSearchKeywordWithCurrency("신발");
        searchLogService.saveSearchKeywordWithCurrency("신발");

        searchLogService.saveSearchKeywordWithCurrency("모자"); // 2번 검색
        searchLogService.saveSearchKeywordWithCurrency("모자");

        searchLogService.saveSearchKeywordWithCurrency("바지"); // 2번 검색
        searchLogService.saveSearchKeywordWithCurrency("바지");

        searchLogService.saveSearchKeywordWithCurrency("시계"); // 1번 검색
        searchLogService.saveSearchKeywordWithCurrency("가방");
        searchLogService.saveSearchKeywordWithCurrency("책");
        searchLogService.saveSearchKeywordWithCurrency("스마트폰");
        searchLogService.saveSearchKeywordWithCurrency("태블릿");
        searchLogService.saveSearchKeywordWithCurrency("노트북");

        // when
        List<String> popularKeywords = searchLogService.getPopularKeywords(10);

        // then
        assertThat(popularKeywords).hasSize(10); // 정확히 10개 가져오는지 검증
        assertThat(popularKeywords).containsExactly(
                "핸드백", "신발", "모자", "바지", "시계", "가방", "책", "스마트폰", "태블릿", "노트북"
        ); // 인기 검색 순위가 올바르게 정렬되었는지 검증
    }


    @Test
    @DisplayName("동시에 10개의 쓰레드가 같은 검색어를 저장할 경우 searchCount가 정상적으로 증가해야 한다.")
    void concurrentSearchKeywordUpdateTest() throws InterruptedException {
        // given
        String keyword = "핸드백";
        int threadCount = 10; // 실행할 쓰레드 개수

        // ExecutorService: 멀티쓰레드 실행 관리
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        // CountDownLatch: 모든 쓰레드가 실행될 때까지 대기하도록 설정
        CountDownLatch latch = new CountDownLatch(threadCount);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    searchLogService.saveSearchKeywordWithCurrency(keyword);
                } finally {
                    latch.countDown(); // 쓰레드 실행 완료 후 countDown
                }
            });
        }

        // 모든 쓰레드가 끝날 때까지 대기
        latch.await();

        // then
        SearchLog log = searchLogRepository.findByKeyword(keyword).orElseThrow();

        assertThat(log.getSearchCount()).isEqualTo(threadCount); // ✅ 검색 횟수가 10인지 검증
    }
}