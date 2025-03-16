package shoppingmall.ankim.domain.viewRolling.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ViewRollingScheduler {

    private final ViewRollingService viewRollingService;

    @Scheduled(cron = "0 0 0 * * *")
    public void rollupDailyViews() {
        log.info("[SCHEDULED] rollupDailyViews 실행됨");
        viewRollingService.rollupRealTimeToDaily();
        viewRollingService.subtractRealTimeViews();
    }

    @Scheduled(cron = "0 0 0 * * SUN")
    public void rollupWeeklyViews() {
        log.info("[SCHEDULED] rollupWeeklyViews 실행됨");
        viewRollingService.rollupDailyToWeekly();
        viewRollingService.subtractDailyViews();
    }

    @Scheduled(cron = "0 0 0 1 * *")
    public void rollupMonthlyViews() {
        log.info("[SCHEDULED] rollupMonthlyViews 실행됨");
        viewRollingService.rollupWeeklyToMonthly();
        viewRollingService.subtractWeeklyViews();
    }
}


