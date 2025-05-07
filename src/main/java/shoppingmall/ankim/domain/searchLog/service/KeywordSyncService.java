package shoppingmall.ankim.domain.searchLog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.searchLog.repository.SearchLogRepository;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeywordSyncService {

    private final RedisSearchLogService redisSearchLogService;
    private final SearchLogRepository searchLogRepository;

    /**
     * Redis ZSET에 있는 keyword-score 전체를 MySQL에 반영
     */
    @Transactional
    public void syncRedisToMySQL() {
        Map<String, Double> keywordMap = redisSearchLogService.getAllKeywordScores();

        for (Map.Entry<String, Double> entry : keywordMap.entrySet()) {
            String keyword = entry.getKey();
            int count = entry.getValue().intValue();

//            searchLogRepository.upsertSearchKeyword(keyword, count);
        }
    }
}

