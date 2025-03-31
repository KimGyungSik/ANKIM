package shoppingmall.ankim.leaning.subject;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

public class TestAsyncScheduler {

    public static CountDownLatch latch = new CountDownLatch(2);

    @Async
    @SchedulerLock(name = "async1", lockAtLeastFor = "PT5S", lockAtMostFor = "PT10S")
    @Scheduled(fixedDelay = 1000)
    public void asyncJob1() {
        System.out.println("🧵 asyncJob1 스레드: " + Thread.currentThread().getName());
        latch.countDown();
    }

    @Async
    @SchedulerLock(name = "async2", lockAtLeastFor = "PT5S", lockAtMostFor = "PT10S")
    @Scheduled(fixedDelay = 1000)
    public void asyncJob2() {
        System.out.println("🧵 asyncJob2 스레드: " + Thread.currentThread().getName());
        latch.countDown();
    }
}

