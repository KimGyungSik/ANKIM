package shoppingmall.ankim.global.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import shoppingmall.ankim.global.config.clock.ClockHolder;

import java.time.Instant;

@TestConfiguration
public class TestClockConfig {

    @Bean
    public TestClockHolder testClockHolder() {
        return new TestClockHolder(Instant.parse("2025-01-10T00:00:00Z").toEpochMilli());
    }

    @Bean
    public ClockHolder clockHolder(TestClockHolder testClockHolder) {
        return testClockHolder;
    }
}

