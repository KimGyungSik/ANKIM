package shoppingmall.ankim.leaning.subject;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class AsyncPollingScheduler {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    @Async
    @Scheduled(fixedRate = 7000) // 7초마다 호출됨
    @SchedulerLock(name = "asyncPolling", lockAtLeastFor = "7s", lockAtMostFor = "7s")
    public void asyncJob() throws InterruptedException {
        String start = LocalTime.now().format(formatter);
        log.info("🟦 [Async] 폴링 시작 - {}", start);

        Thread.sleep(7000); // 7초 걸리는 작업

        String end = LocalTime.now().format(formatter);
        log.info("🟦 [Async] 폴링 종료 - {}", end);
    }
}
