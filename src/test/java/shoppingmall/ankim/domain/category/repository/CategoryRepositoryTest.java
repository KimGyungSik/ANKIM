package shoppingmall.ankim.domain.category.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Commit;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @DisplayName("카테고리에서 중분류와 그에 속한 소분류를 모두 조회할 수 있다")
    @Test
    void findAllMiddleCategoriesWithSubCategories() {
        // given



        // when

        // then
    }


}