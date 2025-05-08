package shoppingmall.ankim;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthCheckController {

    @Autowired
    private Environment env;

    @GetMapping("/ping")
    public String ping() {
        String profile = env.getProperty("SPRING_PROFILES_ACTIVE", "unknown");
        String role = env.getProperty("APP_ROLE", "unknown");
        String external = env.getProperty("EXTERNAL_PORT", "unknown");

        return String.format("✅ 외부포트: %s, 프로필: %s, 역할: %s",
                external, profile, role);
    }
}



