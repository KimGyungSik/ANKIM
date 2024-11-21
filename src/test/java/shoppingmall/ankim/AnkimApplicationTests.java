package shoppingmall.ankim;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
@SpringBootTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
@Transactional
class AnkimApplicationTests {


    @Test
    void contextLoads() {
    }

}
