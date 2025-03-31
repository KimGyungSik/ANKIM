package shoppingmall.ankim.leaning.subject;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
@Slf4j
public class SyncPollingScheduler {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    @Scheduled(fixedRate = 7000) // 7초마다 호출됨
    @SchedulerLock(name = "syncPolling", lockAtLeastFor = "7s", lockAtMostFor = "7s")
    public void syncJob() throws InterruptedException {
        String start = LocalTime.now().format(formatter);
        log.info("🟥 [Sync] 폴링 시작 - {}", start);

        Thread.sleep(7000); // 7초 걸리는 작업

        String end = LocalTime.now().format(formatter);
        log.info("🟥 [Sync] 폴링 종료 - {}", end);
    }
}
