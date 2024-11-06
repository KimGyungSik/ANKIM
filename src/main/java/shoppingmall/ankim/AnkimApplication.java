package shoppingmall.ankim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
//@EnableJpaAuditing
public class AnkimApplication {

    public static void main(String[] args) {

        SpringApplication.run(AnkimApplication.class, args);

        System.out.println("first PR");
    }

}
