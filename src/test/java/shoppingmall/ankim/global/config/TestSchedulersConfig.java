package shoppingmall.ankim.global.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import shoppingmall.ankim.leaning.subject.*;

@TestConfiguration
public class TestSchedulersConfig {

    @Bean
    public AsyncPollingScheduler asyncPollingScheduler() {
        return new AsyncPollingScheduler();
    }
    @Bean
    public SyncPollingScheduler syncPollingScheduler() {
        return new SyncPollingScheduler();
    }
    @Bean
    public SchedulerA schedulerA() {
        return new SchedulerA();
    }

    @Bean
    public SchedulerB schedulerB() {
        return new SchedulerB();
    }

    @Bean
    public TestConflictScheduler testConflictScheduler() {
        return new TestConflictScheduler();
    }

    @Bean
    public TestAsyncScheduler testAsyncScheduler() {
        return new TestAsyncScheduler();
    }
}
