package shoppingmall.ankim.domain.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import shoppingmall.ankim.domain.category.entity.Category;
import shoppingmall.ankim.domain.category.repository.CategoryRepository;
import shoppingmall.ankim.domain.image.entity.ProductImg;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.item.controller.request.ItemCreateRequest;
import shoppingmall.ankim.domain.item.controller.request.ItemDetailRequest;
import shoppingmall.ankim.domain.item.controller.request.ItemUpdateRequest;
import shoppingmall.ankim.domain.item.service.request.ItemDetailServiceRequest;
import shoppingmall.ankim.domain.item.service.request.ItemUpdateServiceRequest;
import shoppingmall.ankim.domain.option.dto.OptionGroupCreateRequest;
import shoppingmall.ankim.domain.option.dto.OptionGroupUpdateRequest;
import shoppingmall.ankim.domain.option.dto.OptionValueCreateRequest;
import shoppingmall.ankim.domain.option.dto.OptionValueUpdateRequest;
import shoppingmall.ankim.domain.option.service.request.OptionGroupUpdateServiceRequest;
import shoppingmall.ankim.domain.option.service.request.OptionValueUpdateServiceRequest;
import shoppingmall.ankim.domain.product.controller.request.ProductCreateRequest;
import shoppingmall.ankim.domain.product.controller.request.ProductUpdateRequest;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.entity.ProductSellingStatus;
import shoppingmall.ankim.domain.product.repository.ProductRepository;
import shoppingmall.ankim.domain.product.service.ProductService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(controllers = ProductController.class)
@ActiveProfiles("test")
class ProductControllerTest {

    @MockBean
    private S3Service s3Service;

    @Autowired
    private MockMvc mockMvc;


    @MockBean // 컨테이너에 Mockito로 만든 Mock객체를 넣어주는 역할
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("신규 상품을 등록한다.")
    @Test
    void createProduct() throws Exception {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
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
                .categoryNo(1L)
                .optionGroups(createOptionGroupRequests())
                .items(createItemRequest())
                .build();

        // JSON 데이터 직렬화
        MockMultipartFile jsonRequest = new MockMultipartFile(
                "productCreateRequest", // Controller의 @RequestPart 이름과 일치
                "", // 원본 파일 이름
                "application/json",
                objectMapper.writeValueAsString(request).getBytes()
        );

        MockMultipartFile thumbnailImage = new MockMultipartFile(
                "thumbnailImages", "thumbnail.jpg", "image/jpeg", "thumbnail data".getBytes());
        MockMultipartFile detailImage = new MockMultipartFile(
                "detailImages", "detail.jpg", "image/jpeg", "detail data".getBytes());

        // when // then
        mockMvc.perform(multipart("/api/v1/products/new")
                        .file(jsonRequest)
                        .file(thumbnailImage)
                        .file(detailImage)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("기존 상품을 수정할 수 있다.")
    @Test
    void updateProduct() throws Exception {
        // given
        Long existingProductId = 1L; // `data.sql`에서 설정된 기존 상품 ID

        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .name("수정된 캐시미어 코트")
                .desc("수정된 상품 설명")
                .discRate(20)
                .origPrice(110000)
                .optYn("Y")
                .restockYn("Y")
                .qty(80)
                .bestYn("Y")
                .freeShip("Y")
                .shipFee(0)
                .searchKeywords("캐시미어, 고급 코트")
                .cauProd("수정된 주의사항")
                .cauOrd("수정된 주문 유의사항")
                .cauShip("수정된 배송 유의사항")
                .categoryNo(2L) // '상의' 카테고리로 변경
                .optionGroups(createOptionGroupUpdateRequests())
                .items(itemUpdateRequest())
                .build();

        // JSON 데이터 직렬화
        MockMultipartFile jsonRequest = new MockMultipartFile(
                "productUpdateRequest",
                "",
                "application/json",
                objectMapper.writeValueAsString(request).getBytes()
        );

        MockMultipartFile updatedThumbnailImage = new MockMultipartFile(
                "thumbnailImages", "updated-thumbnail.jpg", "image/jpeg", "updated thumbnail data".getBytes());
        MockMultipartFile updatedDetailImage1 = new MockMultipartFile(
                "detailImages", "updated-detail1.jpg", "image/jpeg", "updated detail data 1".getBytes());
        MockMultipartFile updatedDetailImage2 = new MockMultipartFile(
                "detailImages", "updated-detail2.jpg", "image/jpeg", "updated detail data 2".getBytes());

        // when // then
        mockMvc.perform(multipart("/api/v1/products/" + existingProductId)
                        .file(jsonRequest)
                        .file(updatedThumbnailImage)
                        .file(updatedDetailImage1)
                        .file(updatedDetailImage2)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(mockRequest -> {
                            mockRequest.setMethod("PUT"); // HTTP 메서드를 PUT으로 설정
                            return mockRequest;
                        }))
                .andDo(print())
                .andExpect(status().isOk());
    }



    private List<OptionGroupUpdateRequest> createOptionGroupUpdateRequests() {
        OptionValueUpdateRequest colorOption1 = OptionValueUpdateRequest.builder()
                .valueName("Blue")
                .colorCode("#0000FF")
                .build();
        OptionValueUpdateRequest colorOption2 = OptionValueUpdateRequest.builder()
                .valueName("Red")
                .colorCode("#FF0000")
                .build();

        OptionGroupUpdateRequest colorGroup = OptionGroupUpdateRequest.builder()
                .groupName("컬러")
                .optionValues(List.of(colorOption1, colorOption2))
                .build();

        OptionValueUpdateRequest sizeOption1 = OptionValueUpdateRequest.builder()
                .valueName("Large")
                .build();
        OptionValueUpdateRequest sizeOption2 = OptionValueUpdateRequest.builder()
                .valueName("Small")
                .build();

        OptionGroupUpdateRequest sizeGroup = OptionGroupUpdateRequest.builder()
                .groupName("사이즈")
                .optionValues(List.of(sizeOption1, sizeOption2))
                .build();

        return List.of(colorGroup, sizeGroup);
    }



    private List<OptionGroupCreateRequest> createOptionGroupRequests() {
        OptionValueCreateRequest colorOption1 = OptionValueCreateRequest.builder()
                .valueName("Blue")
                .colorCode("#0000FF")
                .build();
        OptionValueCreateRequest colorOption2 = OptionValueCreateRequest.builder()
                .valueName("Red")
                .colorCode("#FF0000")
                .build();

        OptionGroupCreateRequest colorGroup = OptionGroupCreateRequest.builder()
                .groupName("컬러")
                .optionValues(List.of(colorOption1, colorOption2))
                .build();

        OptionValueCreateRequest sizeOption1 = OptionValueCreateRequest.builder()
                .valueName("Large")
                .build();
        OptionValueCreateRequest sizeOption2 = OptionValueCreateRequest.builder()
                .valueName("Small")
                .build();

        OptionGroupCreateRequest sizeGroup = OptionGroupCreateRequest.builder()
                .groupName("사이즈")
                .optionValues(List.of(sizeOption1, sizeOption2))
                .build();

        return List.of(colorGroup, sizeGroup);
    }

    private ItemUpdateRequest itemUpdateRequest() {
        return ItemUpdateRequest.builder()
                .items(List.of(
                        ItemDetailRequest.builder()
                                .name("색상: Blue, 사이즈: Small")
                                .optionValueNames(List.of("Blue", "Small"))
                                .addPrice(500)
                                .qty(100)
                                .safQty(10)
                                .maxQty(5)
                                .minQty(1)
                                .build(),
                        ItemDetailRequest.builder()
                                .name("색상: Blue, 사이즈: Large")
                                .optionValueNames(List.of("Blue", "Large"))
                                .addPrice(600)
                                .qty(80)
                                .safQty(5)
                                .maxQty(3)
                                .minQty(1)
                                .build(),
                        ItemDetailRequest.builder()
                                .name("색상: Red, 사이즈: Small")
                                .optionValueNames(List.of("Red", "Small"))
                                .addPrice(700)
                                .qty(120)
                                .safQty(15)
                                .maxQty(7)
                                .minQty(2)
                                .build(),
                        ItemDetailRequest.builder()
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



    private ItemCreateRequest createItemRequest() {
        return ItemCreateRequest.builder()
                .items(List.of(
                        ItemDetailRequest.builder()
                                .name("색상: Blue, 사이즈: Small")
                                .optionValueNames(List.of("Blue", "Small"))
                                .addPrice(500)
                                .qty(100)
                                .safQty(10)
                                .maxQty(5)
                                .minQty(1)
                                .build(),
                        ItemDetailRequest.builder()
                                .name("색상: Blue, 사이즈: Large")
                                .optionValueNames(List.of("Blue", "Large"))
                                .addPrice(600)
                                .qty(80)
                                .safQty(5)
                                .maxQty(3)
                                .minQty(1)
                                .build(),
                        ItemDetailRequest.builder()
                                .name("색상: Red, 사이즈: Small")
                                .optionValueNames(List.of("Red", "Small"))
                                .addPrice(700)
                                .qty(120)
                                .safQty(15)
                                .maxQty(7)
                                .minQty(2)
                                .build(),
                        ItemDetailRequest.builder()
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