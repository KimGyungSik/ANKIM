package shoppingmall.ankim.global.config.track;

import org.springframework.stereotype.Component;

@Component
public class SystemTrackingNumberGenerator implements TrackingNumberGenerator {
    @Override
    public String generate() {
        return "TRCK" + System.currentTimeMillis();
    }
}