package shoppingmall.ankim.domain.searchLog.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.searchLog.entity.SearchLog;
import shoppingmall.ankim.domain.searchLog.repository.SearchLogRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
@TestPropertySource(properties = {
        "spring.sql.init.mode=never",
        "spring.profiles.active=test" // "test" 프로파일 활성화
})
class SearchLogServiceTest {

    @MockBean
    private S3Service s3Service;

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
        searchLogService.saveSearchKeyword(keyword);

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
        searchLogService.saveSearchKeyword(keyword); // 첫 번째 저장
        searchLogService.saveSearchKeyword(keyword); // 두 번째 저장 (횟수 증가)

        // when
        SearchLog log = searchLogRepository.findAll().get(0);

        // then
        assertThat(log.getSearchCount()).isEqualTo(2);
    }

    @DisplayName("인기 검색어를 1등부터 10등까지 가져올 수 있다.")
    @Test
    void getPopularKeywords() {
        // given (각 검색어의 검색 횟수를 다르게 설정)
        searchLogService.saveSearchKeyword("핸드백"); // 4번 검색
        searchLogService.saveSearchKeyword("핸드백");
        searchLogService.saveSearchKeyword("핸드백");
        searchLogService.saveSearchKeyword("핸드백");

        searchLogService.saveSearchKeyword("신발"); // 3번 검색
        searchLogService.saveSearchKeyword("신발");
        searchLogService.saveSearchKeyword("신발");

        searchLogService.saveSearchKeyword("모자"); // 2번 검색
        searchLogService.saveSearchKeyword("모자");

        searchLogService.saveSearchKeyword("바지"); // 2번 검색
        searchLogService.saveSearchKeyword("바지");

        searchLogService.saveSearchKeyword("시계"); // 1번 검색
        searchLogService.saveSearchKeyword("가방");
        searchLogService.saveSearchKeyword("책");
        searchLogService.saveSearchKeyword("스마트폰");
        searchLogService.saveSearchKeyword("태블릿");
        searchLogService.saveSearchKeyword("노트북");

        // when
        List<String> popularKeywords = searchLogService.getPopularKeywords(10);

        // then
        assertThat(popularKeywords).hasSize(10); // 정확히 10개 가져오는지 검증
        assertThat(popularKeywords).containsExactly(
                "핸드백", "신발", "모자", "바지", "시계", "가방", "책", "스마트폰", "태블릿", "노트북"
        ); // 인기 검색 순위가 올바르게 정렬되었는지 검증
    }


}