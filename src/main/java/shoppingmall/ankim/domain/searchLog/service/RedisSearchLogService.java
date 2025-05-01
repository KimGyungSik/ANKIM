package shoppingmall.ankim.domain.searchLog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class RedisSearchLogService {

    private final StringRedisTemplate redisTemplate;
    private static final String SEARCH_KEY = "search:keywords";

    public void saveSearchKeyword(String keyword) {
        // ZINCRBY는 atomic하게 점수를 증가시킴
        redisTemplate.opsForZSet().incrementScore(SEARCH_KEY, keyword, 1.0);
    }

    public List<String> getPopularKeywords(int limit) {
        // 높은 점수 순으로 내림차순 정렬
        Set<String> range = redisTemplate.opsForZSet()
                .reverseRange(SEARCH_KEY, 0, limit - 1);
        return new ArrayList<>(range);
    }

    public Double getKeywordScore(String keyword) {
        return redisTemplate.opsForZSet().score(SEARCH_KEY, keyword);
    }

    public void clearKeywords() {
        redisTemplate.delete(SEARCH_KEY);
    }

    // 배치에서 사용될 메서드
    public Map<String, Double> getAllKeywordScores() {
        Set<ZSetOperations.TypedTuple<String>> entries =
                redisTemplate.opsForZSet().reverseRangeWithScores(SEARCH_KEY, 0, -1);

        Map<String, Double> map = new HashMap<>();
        if (entries != null) {
            for (ZSetOperations.TypedTuple<String> entry : entries) {
                map.put(entry.getValue(), entry.getScore());
            }
        }
        return map;
    }
}

