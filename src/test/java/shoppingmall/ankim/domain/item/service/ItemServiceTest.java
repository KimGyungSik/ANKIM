package shoppingmall.ankim.domain.item.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.category.entity.Category;
import shoppingmall.ankim.domain.category.repository.CategoryRepository;
import shoppingmall.ankim.domain.item.dto.ItemResponse;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
import shoppingmall.ankim.domain.item.service.request.ItemCreateServiceRequest;
import shoppingmall.ankim.domain.option.dto.OptionGroupResponse;
import shoppingmall.ankim.domain.option.entity.OptionGroup;
import shoppingmall.ankim.domain.option.entity.OptionValue;
import shoppingmall.ankim.domain.option.repository.OptionGroupRepository;
import shoppingmall.ankim.domain.option.repository.OptionValueRepository;
import shoppingmall.ankim.domain.option.service.OptionGroupService;
import shoppingmall.ankim.domain.option.service.request.OptionGroupCreateServiceRequest;
import shoppingmall.ankim.domain.option.service.request.OptionValueCreateServiceRequest;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.entity.ProductSellingStatus;
import shoppingmall.ankim.domain.product.repository.ProductRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
class ItemServiceTest {

    @Autowired
    ItemService itemService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    OptionGroupRepository optionGroupRepository;

    @Autowired
    OptionValueRepository optionValueRepository;

    @Autowired
    OptionGroupService optionGroupService;

    @Autowired
    ItemRepository itemRepository;

    @DisplayName("옵션별 품목을 생성할 수 있다.")
    @Test
    void createItemTest() {
        // given
        Product product = createProduct();
        productRepository.save(product);

        List<Long> optionGroupIds = createOptionGroupsAndReturnIds(product);
        System.out.println("optionGroupIds = " + optionGroupIds);

        ItemCreateServiceRequest request = ItemCreateServiceRequest.builder()
                .addPrice(500)
                .qty(10)
                .safQty(2)
                .maxQty(5)
                .minQty(1)
                .build();

        // when
        List<ItemResponse> result = itemService.createItem(product.getNo(), optionGroupIds, request);

        // then
        // 옵션 값의 조합 수와 생성된 품목 수가 같은지 검증
        assertThat(result).hasSize(4)  // 총 4개의 조합
                .extracting("code", "name", "addPrice", "qty", "safQty", "sellingStatus", "maxQty", "minQty")
                .containsExactlyInAnyOrder(
                        tuple(
                                product.getCode() + "-1",         // code (예: PROD123-1)
                                "컬러: Blue, 사이즈: large",       // name (예: 컬러: Blue, 사이즈: large)
                                request.getAddPrice(),            // addPrice
                                request.getQty(),                 // qty
                                request.getSafQty(),              // safQty
                                ProductSellingStatus.SELLING,     // sellingStatus
                                request.getMaxQty(),              // maxQty
                                request.getMinQty()               // minQty
                        ),
                        tuple(
                                product.getCode() + "-2",
                                "컬러: Blue, 사이즈: small",
                                request.getAddPrice(),
                                request.getQty(),
                                request.getSafQty(),
                                ProductSellingStatus.SELLING,
                                request.getMaxQty(),
                                request.getMinQty()
                        ),
                        tuple(
                                product.getCode() + "-3",
                                "컬러: Red, 사이즈: large",
                                request.getAddPrice(),
                                request.getQty(),
                                request.getSafQty(),
                                ProductSellingStatus.SELLING,
                                request.getMaxQty(),
                                request.getMinQty()
                        ),
                        tuple(
                                product.getCode() + "-4",
                                "컬러: Red, 사이즈: small",
                                request.getAddPrice(),
                                request.getQty(),
                                request.getSafQty(),
                                ProductSellingStatus.SELLING,
                                request.getMaxQty(),
                                request.getMinQty()
                        )
                );
    }

    private List<Long> createOptionGroupsAndReturnIds(Product product) {
        OptionGroup colorGroup = optionGroupRepository.save(
                OptionGroup.builder().name("컬러").product(product).build()
        );
        OptionGroup sizeGroup = optionGroupRepository.save(
                OptionGroup.builder().name("사이즈").product(product).build()
        );

        OptionValue blue = OptionValue.builder().optionGroup(colorGroup).name("Blue").colorCode("#0000FF").build();
        OptionValue red = OptionValue.builder().optionGroup(colorGroup).name("Red").colorCode("#FF0000").build();
        OptionValue large = OptionValue.builder().optionGroup(sizeGroup).name("large").build();
        OptionValue small = OptionValue.builder().optionGroup(sizeGroup).name("small").build();

        // 각 OptionGroup에 OptionValue 추가
        colorGroup.addOptionValue(blue);
        colorGroup.addOptionValue(red);
        sizeGroup.addOptionValue(large);
        sizeGroup.addOptionValue(small);

        // 영속성 저장
        optionGroupRepository.save(colorGroup);
        optionGroupRepository.save(sizeGroup);

        return List.of(colorGroup.getNo(), sizeGroup.getNo());
    }


    private Category createCategory() {
        return categoryRepository.save(Category.builder()
                .name("상의")
                .subCategories(List.of(Category.builder()
                        .name("코트")
                        .build()))
                .build());
    }

    private Product createProduct() {
        return Product.builder()
                .category(createCategory())
                .name("테스트 상품")
                .code("PROD123")
                .desc("테스트 상품 설명")
                .discRate(10)
                .sellPrice(10000)
                .origPrice(12000)
                .optYn("Y")
                .restockYn("N")
                .qty(100)
                .bestYn("N")
                .freeShip("Y")
                .shipFee(2500)
                .searchKeywords("테스트")
                .relProdCode("REL001")
                .cauProd("주의사항")
                .cauOrd("주문 유의사항")
                .cauShip("배송 유의사항")
                .build();
    }
}
