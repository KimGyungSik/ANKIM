package shoppingmall.ankim.factory;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import shoppingmall.ankim.domain.image.service.S3Service;

@TestConfiguration
public class MockBeanFactory {
    @Bean
    @Primary
    public S3Service s3Service() {
        return Mockito.mock(S3Service.class);
    }


}
