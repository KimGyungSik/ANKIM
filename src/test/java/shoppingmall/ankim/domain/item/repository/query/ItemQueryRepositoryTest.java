package shoppingmall.ankim.domain.item.repository.query;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.repository.ProductRepository;
import shoppingmall.ankim.factory.ProductFactory;
import shoppingmall.ankim.global.config.QuerydslConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
@Import(QuerydslConfig.class)
class ItemQueryRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    EntityManager em;

    @DisplayName("옵션 값 리스트와 상품 번호를 기반으로 단일 Item 조회")
    @Test
    public void findItemByOptionValuesAndProduct() {
        // given
        Product product = ProductFactory.createProduct(em);


        // OptionValue 번호 리스트 생성
        List<Long> optionValueNoList = product.getItems().get(0).getItemOptions()
                .stream()
                .map(itemOption -> itemOption.getOptionValue().getNo())
                .toList();

        Long productNo = product.getNo();

        // when: 옵션 값 리스트와 상품 번호로 Item 조회
        Item result = itemRepository.findItemByOptionValuesAndProduct(productNo, optionValueNoList);
        System.out.println("result.getName() = " + result.getName());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getProduct().getNo()).isEqualTo(productNo);
        assertThat(result.getItemOptions())
                .hasSize(optionValueNoList.size())
                .extracting(option -> option.getOptionValue().getNo())
                .containsExactlyInAnyOrderElementsOf(optionValueNoList);

    }

}