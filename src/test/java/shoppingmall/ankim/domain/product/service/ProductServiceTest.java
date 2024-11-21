package shoppingmall.ankim.domain.product.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.category.entity.Category;
import shoppingmall.ankim.domain.category.repository.CategoryRepository;
import shoppingmall.ankim.domain.image.service.FileService;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.image.service.request.ProductImgCreateServiceRequest;
import shoppingmall.ankim.domain.image.service.request.ProductImgUpdateServiceRequest;
import shoppingmall.ankim.domain.item.repository.ItemRepository;
import shoppingmall.ankim.domain.item.service.ItemService;
import shoppingmall.ankim.domain.item.service.request.ItemCreateServiceRequest;
import shoppingmall.ankim.domain.item.service.request.ItemDetailServiceRequest;
import shoppingmall.ankim.domain.item.service.request.ItemUpdateServiceRequest;
import shoppingmall.ankim.domain.option.dto.OptionGroupResponse;
import shoppingmall.ankim.domain.option.entity.OptionGroup;
import shoppingmall.ankim.domain.option.repository.OptionGroupRepository;
import shoppingmall.ankim.domain.option.repository.OptionValueRepository;
import shoppingmall.ankim.domain.option.service.OptionGroupService;
import shoppingmall.ankim.domain.option.service.request.OptionGroupCreateServiceRequest;
import shoppingmall.ankim.domain.option.service.request.OptionGroupUpdateServiceRequest;
import shoppingmall.ankim.domain.option.service.request.OptionValueCreateServiceRequest;
import shoppingmall.ankim.domain.option.service.request.OptionValueUpdateServiceRequest;
import shoppingmall.ankim.domain.product.dto.ProductResponse;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.entity.ProductSellingStatus;
import shoppingmall.ankim.domain.product.exception.CannotModifySellingProductException;
import shoppingmall.ankim.domain.product.repository.ProductRepository;
import shoppingmall.ankim.domain.product.service.request.ProductCreateServiceRequest;
import shoppingmall.ankim.domain.product.service.request.ProductUpdateServiceRequest;
import shoppingmall.ankim.global.exception.ErrorCode;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
@Transactional
class ProductServiceTest {

    @MockBean
    S3Service s3Service;

    @MockBean
    FileService fileService;

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

    @DisplayName("판매중인 상품은 수정할 수 없다.")
    @Test
    void updateProductTest() {
        // given
        Category category = createCategory();
        Product product = createProduct(category); // 상품 생성 및 저장
        productRepository.save(product);

        // 새로운 요청 데이터 준비
        ProductUpdateServiceRequest updateRequest = ProductUpdateServiceRequest.builder()
                .name("수정된 상품명")
                .desc("수정된 상세 설명")
                .discRate(15)
                .origPrice(15000)
                .optYn("N")
                .restockYn("Y")
                .qty(200)
                .bestYn("Y")
                .freeShip("N")
                .shipFee(3000)
                .searchKeywords("수정된 키워드")
                .cauProd("수정된 상품 유의사항")
                .cauOrd("수정된 주문 유의사항")
                .cauShip("수정된 배송 유의사항")
                .categoryNo(createNewCategory().getNo()) // 서브카테고리로 변경
                .productImages(productImgUpdateServiceRequest())
                .optionGroups(createOptionGroupUpdateRequests()) // 새로운 옵션 그룹
                .items(itemUpdateServiceRequest()) // 새로운 품목
                .build();

        // when
        CannotModifySellingProductException thrownException = assertThrows(
                CannotModifySellingProductException.class,
                () -> productService.updateProduct(product.getNo(), updateRequest)
        );

        // then
        assertThat(thrownException.getErrorCode()).isEqualTo(ErrorCode.CANNOT_MODIFY_SELLING_PRODUCT);
        assertThat(thrownException.getMessage()).contains("판매 중인 상품은 수정할 수 없습니다.");
    }

    @DisplayName("판매 중이 아닌 상품을 수정할 수 있다.")
    @Test
    void updateNonSellingProductTest() {
        // given
        Category category = createCategory();
        Product product = createProduct(category); // 상품 생성 및 저장

        // 기존 상태 설정 (판매 중지)
        product.changeSellingStatus(ProductSellingStatus.STOP_SELLING);
        productRepository.save(product);

        // 새로운 요청 데이터 준비
        ProductUpdateServiceRequest updateRequest = ProductUpdateServiceRequest.builder()
                .name("수정된 상품명")
                .desc("수정된 상세 설명")
                .discRate(15)
                .origPrice(15000)
                .optYn("N")
                .restockYn("Y")
                .qty(200)
                .bestYn("Y")
                .freeShip("N")
                .shipFee(3000)
                .searchKeywords("수정된 키워드")
                .cauProd("수정된 상품 유의사항")
                .cauOrd("수정된 주문 유의사항")
                .cauShip("수정된 배송 유의사항")
                .categoryNo(createNewCategory().getNo()) // 서브카테고리로 변경
                .productImages(productImgUpdateServiceRequest())
                .optionGroups(createOptionGroupUpdateRequests()) // 새로운 옵션 그룹
                .items(itemUpdateServiceRequest()) // 새로운 품목
                .build();

        // when
        ProductResponse response = productService.updateProduct(product.getNo(), updateRequest);

        // then
        assertThat(response.getName()).isEqualTo("수정된 상품명");
        assertThat(response.getDesc()).isEqualTo("수정된 상세 설명");
        assertThat(response.getProductImgs()).hasSize(2); // 이미지 수정 확인
        assertThat(response.getOptionGroups()).hasSize(2); // 옵션 그룹 수정 확인
        assertThat(itemRepository.findByProduct_No(response.getNo())).hasSize(4); // 품목 수정 확인
    }

    @DisplayName("상품을 삭제할 수 있다.")
    @Rollback(value = false)
    @Test
    @Sql(scripts = "/data.sql", config = @SqlConfig(encoding = "UTF-8"))
    void deleteProduct() {
        // given
        Long productIdToDelete = 1L; // 삭제할 상품 ID

        // Mock S3와 파일 시스템 동작
        doNothing().when(s3Service).deleteFile(anyString());
        doNothing().when(fileService).deleteFile(anyString());

        // when
        productService.deleteProduct(productIdToDelete);

        // then
        assertFalse(productRepository.findById(productIdToDelete).isPresent(), "상품이 삭제되지 않았습니다.");
        verify(s3Service, times(2)).deleteFile(anyString()); // S3 파일 삭제 호출 확인
        verify(fileService, times(2)).deleteFile(anyString()); // 로컬 파일 삭제 호출 확인
    }



    private Product createProduct(Category category) {
        return productRepository.save(Product.builder()
                .category(category)
                .name("기존 상품명")
                .desc("기존 상세 설명")
                .discRate(10)
                .origPrice(12000)
                .qty(100)
                .sellingStatus(ProductSellingStatus.SELLING) // 기본적으로 판매 상태
                .build());
    }



    private Category createCategory() {
        return categoryRepository.save(Category.builder()
                .name("상의")
                .subCategories(List.of(Category.builder()
                        .name("코트")
                        .build()))
                .build());
    }

    private Category createNewCategory() {
        return categoryRepository.save(Category.builder()
                .name("하의")
                .subCategories(List.of(Category.builder()
                        .name("반바지")
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

    private List<OptionGroupUpdateServiceRequest> createOptionGroupUpdateRequests() {
        OptionValueUpdateServiceRequest colorOption1 = OptionValueUpdateServiceRequest.builder()
                .valueName("Blue")
                .colorCode("#0000FF")
                .build();
        OptionValueUpdateServiceRequest colorOption2 = OptionValueUpdateServiceRequest.builder()
                .valueName("Red")
                .colorCode("#FF0000")
                .build();

        OptionGroupUpdateServiceRequest colorGroup = OptionGroupUpdateServiceRequest.builder()
                .groupName("컬러")
                .optionValues(List.of(colorOption1, colorOption2))
                .build();

        OptionValueUpdateServiceRequest sizeOption1 = OptionValueUpdateServiceRequest.builder()
                .valueName("Large")
                .build();
        OptionValueUpdateServiceRequest sizeOption2 = OptionValueUpdateServiceRequest.builder()
                .valueName("Small")
                .build();

        OptionGroupUpdateServiceRequest sizeGroup = OptionGroupUpdateServiceRequest.builder()
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

    private ProductImgUpdateServiceRequest productImgUpdateServiceRequest() {
        // MockMultipartFile 생성
        MockMultipartFile thumbnailImage = new MockMultipartFile(
                "thumbnail", "thumbnail.jpg", "image/jpeg", "thumbnail data".getBytes());
        MockMultipartFile detailImage = new MockMultipartFile(
                "detail", "detail.jpg", "image/jpeg", "detail data".getBytes());

        // ProductImgCreateServiceRequest 객체 생성
        return ProductImgUpdateServiceRequest.builder()
                .thumbnailImages(List.of(thumbnailImage))
                .detailImages(List.of(detailImage))
                .build();
    }

    private ItemCreateServiceRequest itemCreateServiceRequest() {
        return ItemCreateServiceRequest.builder()
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
    }

    private ItemUpdateServiceRequest itemUpdateServiceRequest() {
        return ItemUpdateServiceRequest.builder()
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
    }


}