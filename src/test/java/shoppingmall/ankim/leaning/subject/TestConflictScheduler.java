package shoppingmall.ankim.leaning.subject;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class TestConflictScheduler {

    public static AtomicInteger executedCount = new AtomicInteger(0);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    @Async
    @SchedulerLock(name = "conflictLock", lockAtLeastFor = "PT10S", lockAtMostFor = "PT20S")
    @Scheduled(fixedDelay = 1000)
    public void job1() {
        String now = LocalTime.now().format(formatter);
        System.out.println("🚀 job1 실행됨 - 시간: " + now + " | 스레드: " + Thread.currentThread().getName());
        executedCount.incrementAndGet();
    }

    @Async
    @SchedulerLock(name = "conflictLock", lockAtLeastFor = "PT10S", lockAtMostFor = "PT20S")
    @Scheduled(fixedDelay = 1000)
    public void job2() {
        String now = LocalTime.now().format(formatter);
        System.out.println("🚀 job2 실행됨 - 시간: " + now + " | 스레드: " + Thread.currentThread().getName());
        executedCount.incrementAndGet();
    }
}

