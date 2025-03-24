package shoppingmall.ankim.leaning.subject;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class SchedulerB {

    public static AtomicBoolean executed = new AtomicBoolean(false);
    public static CountDownLatch latch = new CountDownLatch(1);
    public static long executedAt = 0;

    @Async
    @SchedulerLock(name = "sharedLock2", lockAtLeastFor = "PT5S", lockAtMostFor = "PT10S")
    @Scheduled(initialDelay = 200, fixedDelay = Long.MAX_VALUE)
    public void job() {
        if (executed.compareAndSet(false, true)) {
            executedAt = System.currentTimeMillis();
            System.out.println("🔵 [SchedulerB] 실행 - Thread: " + Thread.currentThread().getName() + ", Time: " + executedAt);
            latch.countDown();
        }
    }

    public static void reset() {
        executed.set(false);
        latch = new CountDownLatch(1);
        executedAt = 0;
    }
}

