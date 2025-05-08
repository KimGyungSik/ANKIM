package shoppingmall.ankim;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthCheckController {

    @Value("${app.role}")
    private String appRole;

    @Value("${app.external-port}")
    private String externalPort;


    @GetMapping("/ping")
    public String ping() {
        return String.format("✅ 외부포트: %s, 역할: %s",
                externalPort, appRole);
    }
}


