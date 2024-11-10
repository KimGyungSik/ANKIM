package shoppingmall.ankim.domain.image.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductImgServiceTest {

    @Autowired
    ProductImgService productImgService;

    @MockBean
    FileService fileService;

    @DisplayName("")
    @Test
    void test() {
        // given

        // when

        // then
    }

}