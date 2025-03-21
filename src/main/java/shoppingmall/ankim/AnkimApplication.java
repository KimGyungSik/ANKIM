package shoppingmall.ankim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableRetry
@EnableScheduling
@SpringBootApplication
@EnableCaching
@EnableAsync
public class AnkimApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnkimApplication.class, args);
    }
}
