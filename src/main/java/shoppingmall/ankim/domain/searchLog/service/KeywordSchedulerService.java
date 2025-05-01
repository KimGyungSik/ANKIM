package shoppingmall.ankim.domain.searchLog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeywordSchedulerService {

    private final KeywordSyncService keywordSyncService;
    @Async
    @SchedulerLock(name = "syncSearchKeyword_schedulerLock", lockAtLeastFor = "PT5S", lockAtMostFor = "PT10S")
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정
    public void syncSearchKeywordFromRedisToMySQL() {
        log.info("[SCHEDULED] Redis 인기검색어 MySQL로 동기화 시작");
        keywordSyncService.syncRedisToMySQL();
    }
}
