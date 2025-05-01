package shoppingmall.ankim.domain.searchLog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.searchLog.entity.SearchLog;
import shoppingmall.ankim.domain.searchLog.repository.SearchLogRepository;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchLogService {
    private final RedisSearchLogService redisSearchLogService;
    public void saveSearchKeyword(String keyword) {
        redisSearchLogService.saveSearchKeyword(keyword);
    }

    public List<String> getPopularKeywords(int limit) {
        return redisSearchLogService.getPopularKeywords(limit);
    }

    // MySQL ON DUPLICATE KEY UPDATE 사용 -> mySQL prod
//    public void saveSearchKeywordWithCurrency(String keyword) {
//        searchLogRepository.upsertSearchKeyword(keyword);
//    }

    // h2 local용
//    public void saveSearchKeyword(String keyword) {
//        int updatedRows = searchLogRepository.incrementSearchCount(keyword);
//
//        // 업데이트된 행이 없으면 새로운 검색어를 삽입
//        if (updatedRows == 0) {
//            searchLogRepository.save(new SearchLog(keyword));
//        }
//    }

//    public List<String> getPopularKeywords(int limit) {
//        return searchLogRepository.findTopKeywords(PageRequest.of(0, limit));
//    }
}
