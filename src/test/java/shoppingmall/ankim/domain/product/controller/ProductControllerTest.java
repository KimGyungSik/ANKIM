package shoppingmall.ankim.domain.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import shoppingmall.ankim.domain.image.dto.ProductImgeCreateRequest;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.image.service.request.ProductImgCreateServiceRequest;
import shoppingmall.ankim.domain.item.dto.ItemCreateRequest;
import shoppingmall.ankim.domain.item.service.request.ItemCreateServiceRequest;
import shoppingmall.ankim.domain.option.dto.OptionGroupCreateRequest;
import shoppingmall.ankim.domain.option.dto.OptionValueCreateRequest;
import shoppingmall.ankim.domain.option.service.request.OptionGroupCreateServiceRequest;
import shoppingmall.ankim.domain.option.service.request.OptionValueCreateServiceRequest;
import shoppingmall.ankim.domain.product.controller.request.ProductCreateRequest;
import shoppingmall.ankim.domain.product.dto.ProductResponse;
import shoppingmall.ankim.domain.product.entity.Product;
import shoppingmall.ankim.domain.product.service.ProductService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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

    @DisplayName("상품을 등록할 때 상품명은 필수값이다.")
    @Test
    void createProductWithoutProductName() {
        // given

        // when

        // then
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

    private ItemCreateRequest createItemRequest() {
        return ItemCreateRequest.builder()
                .addPrice(500)
                .qty(10)
                .safQty(2)
                .maxQty(5)
                .minQty(1)
                .build();
    }

}