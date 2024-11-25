package shoppingmall.ankim.domain.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import shoppingmall.ankim.domain.image.dto.ProductImgeCreateRequest;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.item.controller.request.ItemCreateRequest;
import shoppingmall.ankim.domain.item.controller.request.ItemDetailRequest;
import shoppingmall.ankim.domain.option.dto.OptionGroupCreateRequest;
import shoppingmall.ankim.domain.option.dto.OptionValueCreateRequest;
import shoppingmall.ankim.domain.product.controller.request.ProductCreateRequest;
import shoppingmall.ankim.domain.product.entity.ProductSellingStatus;
import shoppingmall.ankim.domain.product.service.ProductService;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductController.class)
@ActiveProfiles("test")
public class ProductControllerValidationTest {
    @MockBean
    private S3Service s3Service;

    @Autowired
    private MockMvc mockMvc;


    @MockBean // 컨테이너에 Mockito로 만든 Mock객체를 넣어주는 역할
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("상품 등록 시 필수 필드가 누락되면 예외가 발생한다.")
    @Test
    void validateProductCreateRequest() throws Exception {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .code("") // 빈 문자열
                .origPrice(-1) // 음수
                .qty(-10) // 음수
                .discRate(120) // 100 초과
                .optYn("Invalid") // 유효하지 않은 값
                .restockYn("Invalid") // 유효하지 않은 값
                .freeShip("Invalid") // 유효하지 않은 값
                .shipFee(-1) // 음수
                .categoryNo(null) // null
                .build();

        // when // then
        mockMvc.perform(
                        multipart("/api/v1/products/new")
                                .file(new MockMultipartFile("productCreateRequest", "", "application/json",
                                        objectMapper.writeValueAsString(request).getBytes()))
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.fieldErrors").isArray())
                .andExpect(jsonPath("$.fieldErrors[*].field").value(
                        containsInAnyOrder(
                                "name",
                                "code",
                                "origPrice",
                                "qty",
                                "discRate",
                                "optYn",
                                "restockYn",
                                "freeShip",
                                "shipFee",
                                "categoryNo"
                        )))
                .andExpect(jsonPath("$.fieldErrors[*].reason").value(
                        containsInAnyOrder(
                                "상품명은 필수 입력 값입니다.",
                                "상품코드는 필수 입력 값입니다.",
                                "정상가격은 0 이상이어야 합니다.",
                                "재고량은 0 이상이어야 합니다.",
                                "할인율은 100 이하이어야 합니다.",
                                "옵션 여부는 'Y' 또는 'N'이어야 합니다.",
                                "재입고 알림 여부는 'Y' 또는 'N'이어야 합니다.",
                                "무료배송 여부는 'Y' 또는 'N'이어야 합니다.",
                                "배송비는 0 이상이어야 합니다.",
                                "카테고리 ID는 필수 입력 값입니다."
                        )))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("옵션 그룹 요청 필수 필드 누락 시 예외가 발생한다.")
    @Test
    void validateOptionGroupCreateRequest() throws Exception {
        // given
        OptionGroupCreateRequest optionGroupRequest = OptionGroupCreateRequest.builder()
                .groupName(null) // 필수 값 누락 (빈 문자열 대신 null로 설정)
                .build();

        ItemDetailRequest itemDetailRequest = ItemDetailRequest.builder() // 품목 상세 요청 객체
                .name(null) // 품목명 필수 값 누락
                .qty(null) // 재고량 필수 값 누락
                .safQty(null) // 안전 재고량 필수 값 누락
                .sellingStatus(null) // 품목 판매상태 필수 값 누락
                .minQty(null) // 최소 구매 수량 필수 값 누락
                .maxQty(null) // 최대 구매 수량 필수 값 누락
                .optionValueNames(null) // 옵션 값 이름 리스트 필수 값 누락
                .build();

        ItemCreateRequest itemRequest = ItemCreateRequest.builder() // 품목 요청 객체
                .items(List.of(itemDetailRequest))
                .build();

        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("테스트 상품")
                .code("PROD123")
                .origPrice(12000)
                .categoryNo(1L)
                .optionGroups(List.of(optionGroupRequest)) // 옵션 그룹 추가
                .items(itemRequest) // 품목 추가
                .build();

        MockMultipartFile jsonRequest = new MockMultipartFile(
                "productCreateRequest",
                "",
                "application/json",
                objectMapper.writeValueAsString(request).getBytes()
        );

        MockMultipartFile jsonItemRequest = new MockMultipartFile(
                "itemRequest",
                "",
                "application/json",
                objectMapper.writeValueAsString(itemRequest).getBytes()
        );

        MockMultipartFile thumbnailImage = new MockMultipartFile(
                "thumbnailImages",
                "thumbnail.jpg",
                "image/jpeg",
                "thumbnail data".getBytes()
        );

        MockMultipartFile detailImage = new MockMultipartFile(
                "detailImages",
                "detail.jpg",
                "image/jpeg",
                "detail data".getBytes()
        );

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/products/new")
                        .file("file", new byte[]{}) // 실제 파일을 전송하려면 여기에 파일을 추가
                        .file(jsonRequest)
                        .file(jsonItemRequest)
                        .file(thumbnailImage)
                        .file(detailImage)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[*].field", containsInAnyOrder(
                        "items.items[0].safQty",
                        "items.items[0].maxQty",
                        "items.items[0].sellingStatus",
                        "items.items[0].qty",
                        "items.items[0].minQty",
                        "items.items[0].optionValueNames",
                        "items.items[0].name",
                        "optionGroups[0].groupName" // 옵션 그룹에 대한 필드 추가
                )))
                .andExpect(jsonPath("$.fieldErrors[*].reason", containsInAnyOrder(
                        "안전 재고량은 필수 입력 값입니다.",
                        "최대 구매 수량은 필수 입력 값입니다.",
                        "품목 판매상태는 필수 입력 값입니다.",
                        "재고량은 필수 입력 값입니다.",
                        "최소 구매 수량은 필수 입력 값입니다.",
                        "옵션 값 이름 리스트는 필수 입력 값입니다.",
                        "품목명은 필수 입력 값입니다.",
                        "옵션항목명은 필수 입력 값입니다." // 옵션 그룹에 대한 오류 메시지 추가
                )));
    }

    @DisplayName("옵션 값 요청 필수 필드 누락 시 예외가 발생한다.")
    @Test
    void validateOptionValueCreateRequest() throws Exception {
        // given
        OptionValueCreateRequest optionValueRequest = new OptionValueCreateRequest();
        OptionGroupCreateRequest optionGroupRequest = OptionGroupCreateRequest.builder()
                .groupName("컬러")
                .optionValues(List.of(optionValueRequest))
                .build();
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("테스트 상품")
                .code("PROD123")
                .origPrice(12000)
                .categoryNo(1L)
                .optionGroups(List.of(optionGroupRequest))
                .build();

        // when // then
        mockMvc.perform(
                        multipart("/api/v1/products/new")
                                .file(new MockMultipartFile("productCreateRequest", "", "application/json",
                                        objectMapper.writeValueAsString(request).getBytes()))
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("optionGroups[0].optionValues[0].valueName"))
                .andExpect(jsonPath("$.fieldErrors[0].reason").value("옵션명은 필수 입력 값입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("상품 이미지 요청 필수 필드 누락 시 예외가 발생한다.")
    @Test
    void validateProductImageCreateRequest() throws Exception {
        // given
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("테스트 상품")
                .code("PROD123")
                .origPrice(12000)
                .categoryNo(1L)
                .build();

        MockMultipartFile jsonRequest = new MockMultipartFile(
                "productCreateRequest", // Controller의 @RequestPart 이름과 일치
                "", // 원본 파일 이름
                "application/json",
                objectMapper.writeValueAsString(request).getBytes() // JSON 직렬화
        );

        // 이미지를 포함하지 않음
        // 썸네일 이미지는 생략하여 누락된 상태를 테스트
        MockMultipartFile detailImage = new MockMultipartFile(
                "detailImages", "detail.jpg", "image/jpeg", "detail data".getBytes()
        );

        // when // then
        mockMvc.perform(
                        multipart("/api/v1/products/new")
                                .file(jsonRequest)
                                .file(detailImage) // thumbnailImages 누락
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("필수 요청 필드가 누락되었습니다."))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("thumbnailImages"))
                .andExpect(jsonPath("$.fieldErrors[0].reason").value("해당 요청 필드는 필수입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("품목 정보가 누락되면 예외가 발생한다.")
    @Test
    void validateItemCreateRequest() throws Exception {
        // given
        ItemCreateRequest itemCreateRequest = ItemCreateRequest.builder()
                .items(null) // null
                .build();

        ProductCreateRequest productRequest = ProductCreateRequest.builder()
                .name("테스트 상품")
                .code("PROD123")
                .origPrice(12000)
                .categoryNo(1L)
                .items(itemCreateRequest)
                .build();

        MockMultipartFile jsonRequest = new MockMultipartFile(
                "productCreateRequest", "", "application/json",
                objectMapper.writeValueAsString(productRequest).getBytes()
        );

        // when // then
        mockMvc.perform(multipart("/api/v1/products/new")
                        .file(jsonRequest)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("items.items"))
                .andExpect(jsonPath("$.fieldErrors[0].reason").value("품목 정보는 필수 입력 값입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void validateItemDetailRequest() throws Exception {
        // given
        ItemDetailRequest itemDetailRequest = ItemDetailRequest.builder().build();

        ItemCreateRequest itemCreateRequest = ItemCreateRequest.builder()
                .items(List.of(itemDetailRequest))
                .build();

        ProductCreateRequest productRequest = ProductCreateRequest.builder()
                .name("테스트 상품")
                .code("PROD123")
                .origPrice(12000)
                .categoryNo(1L)
                .items(itemCreateRequest)
                .build();

        MockMultipartFile jsonRequest = new MockMultipartFile(
                "productCreateRequest", "", "application/json",
                objectMapper.writeValueAsString(productRequest).getBytes()
        );
        MockMultipartFile jsonItemRequest = new MockMultipartFile(
                "itemDetailRequest", "", "application/json",
                objectMapper.writeValueAsString(itemDetailRequest).getBytes()
        );

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/products/new")
                        .file("file", new byte[]{}) // 실제 파일을 전송하려면 여기에 파일을 추가
                        .file(jsonRequest)
                        .file(jsonItemRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[*].field", containsInAnyOrder(
                        "items.items[0].safQty",
                        "items.items[0].maxQty",
                        "items.items[0].sellingStatus",
                        "items.items[0].qty",
                        "items.items[0].minQty",
                        "items.items[0].optionValueNames",
                        "items.items[0].name"
                )))
                .andExpect(jsonPath("$.fieldErrors[*].reason", containsInAnyOrder(
                        "안전 재고량은 필수 입력 값입니다.",
                        "최대 구매 수량은 필수 입력 값입니다.",
                        "품목 판매상태는 필수 입력 값입니다.",
                        "재고량은 필수 입력 값입니다.",
                        "최소 구매 수량은 필수 입력 값입니다.",
                        "옵션 값 이름 리스트는 필수 입력 값입니다.",
                        "품목명은 필수 입력 값입니다."
                )));
    }

}
