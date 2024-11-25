package shoppingmall.ankim.domain.item.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import shoppingmall.ankim.domain.category.entity.Category;
import shoppingmall.ankim.domain.category.repository.CategoryRepository;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.item.dto.ItemPreviewResponse;
import shoppingmall.ankim.domain.item.dto.ItemResponse;
import shoppingmall.ankim.domain.item.entity.Item;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
import shoppingmall.ankim.domain.item.service.request.ItemCreateServiceRequest;
import shoppingmall.ankim.domain.item.service.request.ItemDetailServiceRequest;
import shoppingmall.ankim.domain.item.service.request.ItemUpdateServiceRequest;
import shoppingmall.ankim.domain.option.entity.OptionGroup;
import shoppingmall.ankim.domain.option.entity.OptionValue;
import shoppingmall.ankim.domain.option.repository.OptionGroupRepository;
import shoppingmall.ankim.domain.option.repository.OptionValueRepository;
import shoppingmall.ankim.domain.option.service.request.OptionGroupCreateServiceRequest;
import shoppingmall.ankim.domain.option.service.request.OptionValueCreateServiceRequest;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.entity.ProductSellingStatus;
import shoppingmall.ankim.domain.product.repository.ProductRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false) // CSRF 비활성화
@SpringBootTest
@Transactional
@TestPropertySource(properties = "spring.sql.init.mode=never")
class ItemServiceTest {

    @MockBean
    S3Service s3Service;

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
    ItemRepository itemRepository;

    @DisplayName("옵션 조합 생성 및 미리 보기를 할 수 있다.")
    @Test
    void generateOptionCombinations() {
        // given
        List<OptionGroupCreateServiceRequest> optionGroupRequests = List.of(
                OptionGroupCreateServiceRequest.builder()
                        .groupName("색상")
                        .optionValues(List.of(
                                OptionValueCreateServiceRequest.builder().valueName("Blue").colorCode("#0000FF").build(),
                                OptionValueCreateServiceRequest.builder().valueName("Red").colorCode("#FF0000").build()
                        ))
                        .build(),
                OptionGroupCreateServiceRequest.builder()
                        .groupName("사이즈")
                        .optionValues(List.of(
                                OptionValueCreateServiceRequest.builder().valueName("Small").build(),
                                OptionValueCreateServiceRequest.builder().valueName("Large").build()
                        ))
                        .build()
        );


        // when
        List<ItemPreviewResponse> result = itemService.generateOptionCombinations(optionGroupRequests);

        // then
        assertThat(result).hasSize(4) // 가능한 조합 수 확인
                .extracting("name", "optionValueNames") // 이름과 옵션 값 이름 리스트를 추출
                .containsExactlyInAnyOrder(
                        tuple("색상: Blue, 사이즈: Small", List.of("Blue", "Small")),
                        tuple("색상: Blue, 사이즈: Large", List.of("Blue", "Large")),
                        tuple("색상: Red, 사이즈: Small", List.of("Red", "Small")),
                        tuple("색상: Red, 사이즈: Large", List.of("Red", "Large"))
                );
    }

    @DisplayName("옵션별 품목에 세부 설정을 하여 품목을 등록할 수 있다.")
    @Test
    void createItems() {
        // given
        // 1. 상품 생성
        Product product = createAndSaveProduct();

        // 2. 옵션 그룹 및 옵션 값 생성
        OptionGroup colorGroup = optionGroupRepository.save(
                OptionGroup.builder().name("색상").product(product).build()
        );
        OptionGroup sizeGroup = optionGroupRepository.save(
                OptionGroup.builder().name("사이즈").product(product).build()
        );

        OptionValue blue = optionValueRepository.save(OptionValue.builder().optionGroup(colorGroup).name("Blue").colorCode("#0000FF").build());
        OptionValue red = optionValueRepository.save(OptionValue.builder().optionGroup(colorGroup).name("Red").colorCode("#FF0000").build());
        OptionValue small = optionValueRepository.save(OptionValue.builder().optionGroup(sizeGroup).name("Small").build());
        OptionValue large = optionValueRepository.save(OptionValue.builder().optionGroup(sizeGroup).name("Large").build());

        // 3. 품목 생성 요청 데이터
        ItemCreateServiceRequest request = ItemCreateServiceRequest.builder()
                .items(List.of(
                        ItemDetailServiceRequest.builder()
                                .name("색상: Blue, 사이즈: Small")
                                .optionValueNames(List.of("Blue", "Small"))
                                .addPrice(500)
                                .qty(100)
                                .safQty(10)
                                .maxQty(5)
                                .minQty(1)
                                .build(),
                        ItemDetailServiceRequest.builder()
                                .name("색상: Blue, 사이즈: Large")
                                .optionValueNames(List.of("Blue", "Large"))
                                .addPrice(600)
                                .qty(80)
                                .safQty(5)
                                .maxQty(3)
                                .minQty(1)
                                .build(),
                        ItemDetailServiceRequest.builder()
                                .name("색상: Red, 사이즈: Small")
                                .optionValueNames(List.of("Red", "Small"))
                                .addPrice(700)
                                .qty(120)
                                .safQty(15)
                                .maxQty(7)
                                .minQty(2)
                                .build(),
                        ItemDetailServiceRequest.builder()
                                .name("색상: Red, 사이즈: Large")
                                .optionValueNames(List.of("Red", "Large"))
                                .addPrice(800)
                                .qty(60)
                                .safQty(8)
                                .maxQty(4)
                                .minQty(1)
                                .build()
                ))
                .build();

        // when
        List<ItemResponse> result = itemService.createItems(product.getNo(), request);

        // then
        assertThat(result).hasSize(4) // 생성된 품목의 개수 확인
                .extracting("name", "addPrice", "qty", "safQty", "maxQty", "minQty")
                .containsExactlyInAnyOrder(
                        tuple("색상: Blue, 사이즈: Small", 500, 100, 10, 5, 1),
                        tuple("색상: Blue, 사이즈: Large", 600, 80, 5, 3, 1),
                        tuple("색상: Red, 사이즈: Small", 700, 120, 15, 7, 2),
                        tuple("색상: Red, 사이즈: Large", 800, 60, 8, 4, 1)
                );

        // 추가 검증: 데이터베이스에 저장된 품목 확인
        List<Item> savedItems = itemRepository.findAll();
        assertThat(savedItems).hasSize(4);
        assertThat(savedItems).extracting("product").containsOnly(product); // 모든 품목이 동일한 상품과 연결됨
        // 추가 검증: 상품의 품목 리스트에 잘 추가되었는지 확인
        Product updatedProduct = productRepository.findById(product.getNo())
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        assertThat(updatedProduct.getItems()).hasSize(4)
                .extracting("name", "addPrice", "qty", "safQty", "maxQty", "minQty")
                .containsExactlyInAnyOrder(
                        tuple("색상: Blue, 사이즈: Small", 500, 100, 10, 5, 1),
                        tuple("색상: Blue, 사이즈: Large", 600, 80, 5, 3, 1),
                        tuple("색상: Red, 사이즈: Small", 700, 120, 15, 7, 2),
                        tuple("색상: Red, 사이즈: Large", 800, 60, 8, 4, 1)
                );
    }

    @DisplayName("원하는 옵션 조합만 선택해서 품목을 등록할 수 있다.")
    @Test
    void createItemsSelectItem() {
        // given
        // 1. 상품 생성
        Product product = createAndSaveProduct();

        // 2. 옵션 그룹 및 옵션 값 생성
        OptionGroup colorGroup = optionGroupRepository.save(
                OptionGroup.builder().name("색상").product(product).build()
        );
        OptionGroup sizeGroup = optionGroupRepository.save(
                OptionGroup.builder().name("사이즈").product(product).build()
        );

        OptionValue blue = optionValueRepository.save(OptionValue.builder().optionGroup(colorGroup).name("Blue").colorCode("#0000FF").build());
        OptionValue red = optionValueRepository.save(OptionValue.builder().optionGroup(colorGroup).name("Red").colorCode("#FF0000").build());
        OptionValue small = optionValueRepository.save(OptionValue.builder().optionGroup(sizeGroup).name("Small").build());
        OptionValue large = optionValueRepository.save(OptionValue.builder().optionGroup(sizeGroup).name("Large").build());

        // 3. 품목 생성 요청 데이터
        ItemCreateServiceRequest request = ItemCreateServiceRequest.builder()
                .items(List.of(
                        ItemDetailServiceRequest.builder()
                                .name("색상: Blue, 사이즈: Small")
                                .optionValueNames(List.of("Blue", "Small"))
                                .addPrice(500)
                                .qty(100)
                                .safQty(10)
                                .maxQty(5)
                                .minQty(1)
                                .build(),
                        ItemDetailServiceRequest.builder()
                                .name("색상: Red, 사이즈: Small")
                                .optionValueNames(List.of("Red", "Small"))
                                .addPrice(700)
                                .qty(120)
                                .safQty(15)
                                .maxQty(7)
                                .minQty(2)
                                .build(),
                        ItemDetailServiceRequest.builder()
                                .name("색상: Red, 사이즈: Large")
                                .optionValueNames(List.of("Red", "Large"))
                                .addPrice(800)
                                .qty(60)
                                .safQty(8)
                                .maxQty(4)
                                .minQty(1)
                                .build()
                ))
                .build();

        // when
        List<ItemResponse> result = itemService.createItems(product.getNo(), request);

        // then
        assertThat(result).hasSize(3) // 생성된 품목의 개수 확인
                .extracting("name", "addPrice", "qty", "safQty", "maxQty", "minQty")
                .containsExactlyInAnyOrder(
                        tuple("색상: Blue, 사이즈: Small", 500, 100, 10, 5, 1),
                        tuple("색상: Red, 사이즈: Small", 700, 120, 15, 7, 2),
                        tuple("색상: Red, 사이즈: Large", 800, 60, 8, 4, 1)
                );

        // 추가 검증: 데이터베이스에 저장된 품목 확인
        List<Item> savedItems = itemRepository.findAll();
        assertThat(savedItems).hasSize(3);
        assertThat(savedItems).extracting("product").containsOnly(product); // 모든 품목이 동일한 상품과 연결됨
        // 추가 검증: 상품의 품목 리스트에 잘 추가되었는지 확인
        Product updatedProduct = productRepository.findById(product.getNo())
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        assertThat(updatedProduct.getItems()).hasSize(3)
                .extracting("name", "addPrice", "qty", "safQty", "maxQty", "minQty")
                .containsExactlyInAnyOrder(
                        tuple("색상: Blue, 사이즈: Small", 500, 100, 10, 5, 1),
                        tuple("색상: Red, 사이즈: Small", 700, 120, 15, 7, 2),
                        tuple("색상: Red, 사이즈: Large", 800, 60, 8, 4, 1)
                );
    }

    @DisplayName("기존 품목을 업데이트 할 수 있다.")
    @Test
    void updateItemsOnlyUpdate() {
        // given
        Product product = createAndSaveProduct();
        OptionGroup colorGroup = createAndSaveOptionGroup(product, "색상", List.of("Blue", "Red"));
        OptionGroup sizeGroup = createAndSaveOptionGroup(product, "사이즈", List.of("Small", "Large"));

        Item existingItem = itemRepository.save(Item.create(product,
                List.of(findOptionValue(colorGroup, "Blue"), findOptionValue(sizeGroup, "Small")),
                "PROD123-1", "색상: Blue, 사이즈: Small", 500, 100, 10, 5, 1));

        ItemUpdateServiceRequest updateRequest = ItemUpdateServiceRequest.builder()
                .items(List.of(
                        ItemDetailServiceRequest.builder()
                                .name("색상: Blue, 사이즈: Small")
                                .optionValueNames(List.of("Blue", "Small"))
                                .addPrice(600) // 변경된 값
                                .qty(90)
                                .safQty(15)
                                .maxQty(4)
                                .minQty(1)
                                .build()
                ))
                .build();

        // when
        itemService.updateItems(product.getNo(), updateRequest);

        // then
        List<Item> updatedItems = itemRepository.findByProduct_No(product.getNo());
        assertThat(updatedItems).hasSize(1)
                .extracting("name", "addPrice", "qty", "safQty", "maxQty", "minQty")
                .containsExactlyInAnyOrder(
                        tuple("색상: Blue, 사이즈: Small", 600, 90, 15, 4, 1)
                );
    }

    @DisplayName("새로운 품목을 추가할 수 있다.")
    @Test
    void updateItemsOnlyCreate() {
        // given
        Product product = createAndSaveProduct();
        OptionGroup colorGroup = createAndSaveOptionGroup(product, "색상", List.of("Blue", "Red"));
        OptionGroup sizeGroup = createAndSaveOptionGroup(product, "사이즈", List.of("Small", "Large"));

        ItemUpdateServiceRequest createRequest = ItemUpdateServiceRequest.builder()
                .items(List.of(
                        ItemDetailServiceRequest.builder()
                                .name("색상: Red, 사이즈: Large")
                                .optionValueNames(List.of("Red", "Large"))
                                .addPrice(800)
                                .qty(60)
                                .safQty(10)
                                .maxQty(4)
                                .minQty(2)
                                .build()
                ))
                .build();

        // when
        itemService.updateItems(product.getNo(), createRequest);

        // then
        List<Item> createdItems = itemRepository.findByProduct_No(product.getNo());
        assertThat(createdItems).hasSize(1)
                .extracting("name", "addPrice", "qty", "safQty", "maxQty", "minQty")
                .containsExactlyInAnyOrder(
                        tuple("색상: Red, 사이즈: Large", 800, 60, 10, 4, 2)
                );
    }

    @DisplayName("요청에 들어오지 않은 기존 품목을 삭제할 수 있다.")
    @Test
    void updateItemsWithDelete() {
        // given
        Product product = createAndSaveProduct();
        OptionGroup colorGroup = createAndSaveOptionGroup(product, "색상", List.of("Blue", "Red"));
        OptionGroup sizeGroup = createAndSaveOptionGroup(product, "사이즈", List.of("Small", "Large"));

        Item existingItem1 = itemRepository.save(Item.create(product,
                List.of(findOptionValue(colorGroup, "Blue"), findOptionValue(sizeGroup, "Small")),
                "PROD123-1", "색상: Blue, 사이즈: Small", 500, 100, 10, 5, 1));

        Item existingItem2 = itemRepository.save(Item.create(product,
                List.of(findOptionValue(colorGroup, "Red"), findOptionValue(sizeGroup, "Large")),
                "PROD123-2", "색상: Red, 사이즈: Large", 800, 60, 10, 4, 2));

        ItemUpdateServiceRequest deleteRequest = ItemUpdateServiceRequest.builder()
                .items(List.of(
                        ItemDetailServiceRequest.builder()
                                .name("색상: Blue, 사이즈: Small")
                                .optionValueNames(List.of("Blue", "Small"))
                                .addPrice(500)
                                .qty(100)
                                .safQty(10)
                                .maxQty(5)
                                .minQty(1)
                                .build()
                ))
                .build();

        // when
        itemService.updateItems(product.getNo(), deleteRequest);

        // then
        List<Item> remainingItems = itemRepository.findByProduct_No(product.getNo());
        assertThat(remainingItems).hasSize(1)
                .extracting("name")
                .containsExactly("색상: Blue, 사이즈: Small");
    }

    @DisplayName("품목 수정 시 업데이트, 생성, 삭제가 동시에 발생하는 경우에 모두 처리할 수 있다.")
    @Test
    void updateItemsMixed() {
        // given
        Product product = createAndSaveProduct();
        OptionGroup colorGroup = createAndSaveOptionGroup(product, "색상", List.of("Blue", "Red"));
        OptionGroup sizeGroup = createAndSaveOptionGroup(product, "사이즈", List.of("Small", "Large"));

        itemRepository.save(Item.create(product,
                List.of(findOptionValue(colorGroup, "Blue"), findOptionValue(sizeGroup, "Small")),
                "PROD123-1", "색상: Blue, 사이즈: Small", 500, 100, 10, 5, 1));

        ItemUpdateServiceRequest mixedRequest = ItemUpdateServiceRequest.builder()
                .items(List.of(
                        ItemDetailServiceRequest.builder()
                                .name("색상: Blue, 사이즈: Small")
                                .optionValueNames(List.of("Blue", "Small"))
                                .addPrice(550) // 업데이트
                                .qty(110)
                                .safQty(15)
                                .maxQty(6)
                                .minQty(1)
                                .build(),
                        ItemDetailServiceRequest.builder()
                                .name("색상: Red, 사이즈: Small")
                                .optionValueNames(List.of("Red", "Small"))
                                .addPrice(700) // 생성
                                .qty(120)
                                .safQty(20)
                                .maxQty(8)
                                .minQty(2)
                                .build()
                ))
                .build();

        // when
        itemService.updateItems(product.getNo(), mixedRequest);

        // then
        List<Item> updatedItems = itemRepository.findByProduct_No(product.getNo());
        assertThat(updatedItems).hasSize(2)
                .extracting("name", "addPrice", "qty", "safQty", "maxQty", "minQty")
                .containsExactlyInAnyOrder(
                        tuple("색상: Blue, 사이즈: Small", 550, 110, 15, 6, 1),
                        tuple("색상: Red, 사이즈: Small", 700, 120, 20, 8, 2)
                );
    }

    private OptionValue findOptionValue(OptionGroup group, String valueName) {
        return  optionValueRepository.findByOptionGroupNoAndName(group.getNo(), valueName)
                .orElseThrow(() -> new IllegalArgumentException("옵션 값이 존재하지 않습니다."));
    }

    private OptionGroup createAndSaveOptionGroup(Product product, String groupName, List<String> valueNames) {
        OptionGroup group = optionGroupRepository.save(
                OptionGroup.builder().name(groupName).product(product).build()
        );
        valueNames.forEach(name -> optionValueRepository.save(OptionValue.builder().optionGroup(group).name(name).build()));
        return group;
    }

    private Category createCategory() {
        return categoryRepository.save(Category.builder()
                .name("상의")
                .subCategories(List.of(Category.builder()
                        .name("코트")
                        .build()))
                .build());
    }
    private Product createAndSaveProduct() {
        Product product = Product.builder()
                .category(createCategory())
                .name("테스트 상품")
                .code("PROD123")
                .desc("테스트 상품 설명")
                .discRate(10)
                .origPrice(12000)
                .optYn("Y")
                .restockYn("N")
                .qty(100)
                .handMadeYn("N")
                .freeShip("Y")
                .shipFee(2500)
                .searchKeywords("테스트")
                .relProdCode("REL001")
                .cauProd("주의사항")
                .cauOrd("주문 유의사항")
                .cauShip("배송 유의사항")
                .build();
        return productRepository.save(product);
    }
}
