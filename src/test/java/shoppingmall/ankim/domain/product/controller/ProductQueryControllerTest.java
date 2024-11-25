package shoppingmall.ankim.domain.product.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.product.dto.ProductListResponse;
import shoppingmall.ankim.domain.product.dto.ProductResponse;
import shoppingmall.ankim.domain.product.dto.ProductUserDetailResponse;
import shoppingmall.ankim.domain.product.repository.ProductRepository;
import shoppingmall.ankim.domain.product.repository.query.helper.Condition;
import shoppingmall.ankim.domain.product.repository.query.helper.OrderBy;
import shoppingmall.ankim.domain.product.repository.query.helper.PriceCondition;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductQueryController.class)
@ActiveProfiles("test")
class ProductQueryControllerTest {
    @MockBean
    private S3Service s3Service;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    @DisplayName("유저들을 위한 상품 상세 페이지를 조회할 수 있다.")
    @Test
    void findProductUserDetailResponse() throws Exception {
        // given
        ProductUserDetailResponse mockResponse = new ProductUserDetailResponse();

        when(productRepository.findUserProductDetailResponse(anyLong()))
                .thenReturn(mockResponse);
        
        // when // then
        mockMvc.perform(get("/api/v1/product/catalog/{productId}",1L )
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()) // 응답 HTTP 상태 코드 검증
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @DisplayName("관리자들을 위한 상품 상세 페이지를 조회할 수 있다.")
    @Test
    void adminDetailProductResponse() throws Exception {
        // given
        ProductResponse mockResponse = new ProductResponse();

        when(productRepository.findAdminProductDetailResponse(anyLong()))
                .thenReturn(mockResponse);

        // when // then
        mockMvc.perform(get("/api/v1/product/catalog/admin/{productId}",1L )
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()) // 응답 HTTP 상태 코드 검증
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @DisplayName("유저들은 원하는 필터 조건, 카테고리, 정렬을 선택해 나열한 상품들을 볼 수 있다.")
    @Test
    void getFilteredAndSortedProducts() throws Exception {
        // given
        List<ProductListResponse> responseList = List.of(
                new ProductListResponse(/* Mock 데이터 */),
                new ProductListResponse(/* Mock 데이터 */)
        );

        PageRequest pageRequest = PageRequest.of(0, 10); // 첫 페이지, 사이즈 10
        Page<ProductListResponse> list = new PageImpl<>(responseList, pageRequest, 50); // total = 50

        given(productRepository.findUserProductListResponse(
                any(Pageable.class),
                isNull(), // null이 예상되는 경우
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull()
        )).willReturn(list);


        // when // then
        mockMvc.perform(get("/api/v1/product/catalog/list")
                        .param("page", "0") // 첫 번째 페이지
                        .param("size", "10") // 페이지당 10개
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()) // HTTP 상태 코드 200 확인
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.totalElements").value(50)) // 전체 데이터 개수 확인
                .andExpect(jsonPath("$.data.totalPages").value(5)) // 총 페이지 개수 확인
                .andExpect(jsonPath("$.data.size").value(10)) // 페이지당 데이터 개수 확인
                .andExpect(jsonPath("$.data.number").value(0)) // 현재 페이지 번호 확인
                .andExpect(jsonPath("$.data.content").isNotEmpty()) // 데이터가 존재하는지 확인
                .andExpect(jsonPath("$.data.content").isArray()); // 데이터가 리스트인지 확인
    }

}