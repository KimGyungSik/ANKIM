package shoppingmall.ankim.domain.category.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CategoryServiceImplTest {

    @Autowired
    CategoryService categoryService;

    @DisplayName("카테고리에 새로운 중분류를 추가할 수 있다")
    @Test
    void save1() {
        // given

        // when

        // then
    }

    @DisplayName("카테고리에 이미 존재하는 중분류에 소분류를 추가할 수 있다")
    @Test
    void save2() {
        // given

        // when

        // then
    }

    @DisplayName("카테고리에 중분류와 소분류를 동시에 추가할 수 있다")
    @Test
    void save3() {
        // given

        // when

        // then
    }

    @DisplayName("카테고리에 기존 소분류를 다른 중분류로 재배치할 수 있다")
    @Test
    void save4() {
        // given

        // when

        // then
    }
}