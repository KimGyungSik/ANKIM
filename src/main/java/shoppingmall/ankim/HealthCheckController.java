package shoppingmall.ankim;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthCheckController {

    @Value("${server.port}")
    private String port;

    @Value("${spring.profiles.active}")
    private String profile;

    @Value("${APP_ROLE:unknown}")
    private String appRole;

    @Value("${EXTERNAL_PORT:unknown}")
    private String externalPort;

    @GetMapping("/ping")
    public String ping() {
        return String.format("✅ 외부포트: %s, 내부포트: %s, 프로필: %s, 역할: %s",
                externalPort, port, profile, appRole);
    }
}


