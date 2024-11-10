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

    @DisplayName("썸네일 이미지와 상세 이미지를 저장할 수 있다.")
    @Test
    void createProductImgs() {
        // given

        // when

        // then
    }

    @DisplayName("썸네일 이미지와 상세 이미지는 1장은 필수로 등록해야한다.")
    @Test
    void createProductImgsRequired() {
        // given

        // when

        // then
    }

    @DisplayName("썸네일 이미지와 상세 이미지는 최대 6장까지 등록할 수 있다.")
    @Test
    void createProductImgsLimitExceeded() {
        // given

        // when

        // then
    }

    @DisplayName("썸네일 이미지와 상세 이미지는 등록한 순서대로 이미지 순서가 정해진다.")
    @Test
    void createProductImgsOrder() {
        // given

        // when

        // then
    }

}