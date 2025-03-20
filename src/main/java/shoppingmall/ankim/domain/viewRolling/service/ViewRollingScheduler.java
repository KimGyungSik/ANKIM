package shoppingmall.ankim.domain.viewRolling.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ViewRollingScheduler {

    private final ViewRollingService viewRollingService;

    // TODO 만약 redis 도입시 실시간 폴링 작업은 1분마다 이루어져야함 -> fixedRate or fixedDelay

    @Async
    @SchedulerLock(name = "rollupDailyViews_schedulerLock", lockAtLeastFor = "PT5S", lockAtMostFor = "PT10S")
    @Scheduled(cron = "0 0 0 * * *")
    public void rollupDailyViews() {
        log.info("[SCHEDULED] rollupDailyViews 실행됨");
        viewRollingService.rollupRealTimeToDaily();
        viewRollingService.subtractRealTimeViews();
    }
    @Async
    @SchedulerLock(name = "rollupWeeklyViews_schedulerLock", lockAtLeastFor = "PT5S", lockAtMostFor = "PT10S")
    @Scheduled(cron = "0 0 0 * * SUN")
    public void rollupWeeklyViews() {
        log.info("[SCHEDULED] rollupWeeklyViews 실행됨");
        viewRollingService.rollupDailyToWeekly();
        viewRollingService.subtractDailyViews();
    }
    @Async
    @SchedulerLock(name = "rollupMonthlyViews_schedulerLock", lockAtLeastFor = "PT5S", lockAtMostFor = "PT10S")
    @Scheduled(cron = "0 0 0 1 * *")
    public void rollupMonthlyViews() {
        log.info("[SCHEDULED] rollupMonthlyViews 실행됨");
        viewRollingService.rollupWeeklyToMonthly();
        viewRollingService.subtractWeeklyViews();
    }
}


