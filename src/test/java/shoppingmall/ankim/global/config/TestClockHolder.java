package shoppingmall.ankim.global.config;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import shoppingmall.ankim.global.config.clock.ClockHolder;

@AllArgsConstructor
public class TestClockHolder implements ClockHolder {

    private long millis;

    @Override
    public long millis() {
        return millis;
    }

    public void changeTime(long millis) {
        this.millis = millis;
    }
}

