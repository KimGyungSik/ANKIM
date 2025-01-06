package shoppingmall.ankim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableRetry
@SpringBootApplication
@EnableAsync
public class AnkimApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnkimApplication.class, args);
    }
}
