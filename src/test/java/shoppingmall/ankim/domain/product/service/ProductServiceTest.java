package shoppingmall.ankim.domain.product.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.category.entity.Category;
import shoppingmall.ankim.domain.category.repository.CategoryRepository;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.image.service.request.ProductImgCreateServiceRequest;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
import shoppingmall.ankim.domain.item.service.ItemService;
import shoppingmall.ankim.domain.item.service.request.ItemCreateServiceRequest;
import shoppingmall.ankim.domain.option.dto.OptionGroupResponse;
import shoppingmall.ankim.domain.option.entity.OptionGroup;
import shoppingmall.ankim.domain.option.repository.OptionGroupRepository;
import shoppingmall.ankim.domain.option.repository.OptionValueRepository;
import shoppingmall.ankim.domain.option.service.OptionGroupService;
import shoppingmall.ankim.domain.option.service.request.OptionGroupCreateServiceRequest;
import shoppingmall.ankim.domain.option.service.request.OptionValueCreateServiceRequest;
import shoppingmall.ankim.domain.product.dto.ProductResponse;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.repository.ProductRepository;
import shoppingmall.ankim.domain.product.service.request.ProductCreateServiceRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@ActiveProfiles("test")
@SpringBootTest
class ProductServiceTest {

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
    OptionGroupService optionGroupService;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ProductService productService;

    @DisplayName("상품을 등록할 수 있다.")
    @Test
    void createProductTest() {
        // given
        Category category = createCategory();
        List<OptionGroupCreateServiceRequest> optionGroups = createOptionGroupRequests();

        ProductCreateServiceRequest request = ProductCreateServiceRequest.builder()
                .categoryNo(category.getNo())
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
                .productImages(productImgCreateServiceRequest())
                .optionGroups(optionGroups)
                .items(itemCreateServiceRequest())
                .build();

        // when
        ProductResponse response = productService.createProduct(request);

        // then
        assertThat(response.getName()).isEqualTo("테스트 상품");
        assertThat(response.getOptionGroups()).hasSize(2); // 옵션 그룹 수 확인

        List<OptionGroupResponse> groupResponses = response.getOptionGroups();
        assertThat(groupResponses).hasSize(2)
                .extracting("groupName")
                .containsExactlyInAnyOrder("컬러", "사이즈");

        assertThat(itemRepository.findByProduct_No(response.getNo())).hasSize(4); // 4개의 조합 확인
        assertThat(response.getProductImgs()).hasSize(2); // 이미지 수 확인
    }

    private Category createCategory() {
        return categoryRepository.save(Category.builder()
                .name("상의")
                .subCategories(List.of(Category.builder()
                        .name("코트")
                        .build()))
                .build());
    }

    private List<OptionGroupCreateServiceRequest> createOptionGroupRequests() {
        OptionValueCreateServiceRequest colorOption1 = OptionValueCreateServiceRequest.builder()
                .valueName("Blue")
                .colorCode("#0000FF")
                .build();
        OptionValueCreateServiceRequest colorOption2 = OptionValueCreateServiceRequest.builder()
                .valueName("Red")
                .colorCode("#FF0000")
                .build();

        OptionGroupCreateServiceRequest colorGroup = OptionGroupCreateServiceRequest.builder()
                .groupName("컬러")
                .optionValues(List.of(colorOption1, colorOption2))
                .build();

        OptionValueCreateServiceRequest sizeOption1 = OptionValueCreateServiceRequest.builder()
                .valueName("Large")
                .build();
        OptionValueCreateServiceRequest sizeOption2 = OptionValueCreateServiceRequest.builder()
                .valueName("Small")
                .build();

        OptionGroupCreateServiceRequest sizeGroup = OptionGroupCreateServiceRequest.builder()
                .groupName("사이즈")
                .optionValues(List.of(sizeOption1, sizeOption2))
                .build();

        return List.of(colorGroup, sizeGroup);
    }

    private ProductImgCreateServiceRequest productImgCreateServiceRequest() {
        // MockMultipartFile 생성
        MockMultipartFile thumbnailImage = new MockMultipartFile(
                "thumbnail", "thumbnail.jpg", "image/jpeg", "thumbnail data".getBytes());
        MockMultipartFile detailImage = new MockMultipartFile(
                "detail", "detail.jpg", "image/jpeg", "detail data".getBytes());

        // ProductImgCreateServiceRequest 객체 생성
        return ProductImgCreateServiceRequest.builder()
                .thumbnailImages(List.of(thumbnailImage))
                .detailImages(List.of(detailImage))
                .build();
    }

    private ItemCreateServiceRequest itemCreateServiceRequest() {
        return ItemCreateServiceRequest.builder()
                .addPrice(500)
                .qty(10)
                .safQty(2)
                .maxQty(5)
                .minQty(1)
                .build();
    }


}