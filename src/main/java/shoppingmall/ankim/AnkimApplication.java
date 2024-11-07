package shoppingmall.ankim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "shoppingmall.ankim.domain.category.repository") // 리포지토리 패키지 경로
public class AnkimApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnkimApplication.class, args);
    }
}
