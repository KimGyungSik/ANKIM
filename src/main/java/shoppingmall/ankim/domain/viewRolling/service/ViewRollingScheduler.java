package shoppingmall.ankim.domain.viewRolling.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ViewRollingScheduler {

    private final ViewRollingService viewRollingService;

    @Scheduled(cron = "0 0 0 * * *")
    public void rollupDailyViews() {
        viewRollingService.rollupRealTimeToDaily(); // REALTIME → DAILY 롤업
        viewRollingService.subtractRealTimeViews(); // 롤업한 만큼 REALTIME에서 차감
    }

    @Scheduled(cron = "0 0 0 * * SUN")
    public void rollupWeeklyViews() {
        viewRollingService.rollupDailyToWeekly(); // DAILY → WEEKLY 롤업
        viewRollingService.subtractDailyViews(); // 롤업한 만큼 DAILY에서 차감
    }

    @Scheduled(cron = "0 0 0 1 * *")
    public void rollupMonthlyViews() {
        viewRollingService.rollupWeeklyToMonthly(); // WEEKLY → MONTHLY 롤업
        viewRollingService.subtractWeeklyViews(); // 롤업한 만큼 WEEKLY에서 차감
    }
}

