package shoppingmall.ankim.domain.product.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProductApiQueryController.class)
@AutoConfigureMockMvc(addFilters = false) // CSRF 비활성화
@ActiveProfiles("test")
class ProductApiQueryControllerTest {
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

        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<ProductListResponse> pageResult = new PageImpl<>(responseList, pageRequest, 50); // Mock 페이징 데이터

        // 페이징 정보 포함된 JSON 응답 구조를 맞추기 위한 Mock 설정
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("products", responseList);
        mockResponse.put("pageInfo", Map.of(
                "currentPage", 0,
                "totalPages", 5,
                "totalElements", 50,
                "size", 10,
                "hasNext", true,
                "hasPrevious", false
        ));

        given(productRepository.findUserProductListResponse(
                any(Pageable.class),
                any(), any(), any(), any(),
                any(), any(), any(), any(), any()
        )).willReturn(pageResult);


        // when // then
        mockMvc.perform(get("/api/v1/product/catalog/list")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.pageInfo.totalElements").value(50))
                .andExpect(jsonPath("$.data.pageInfo.totalPages").value(5))
                .andExpect(jsonPath("$.data.pageInfo.size").value(10))
                .andExpect(jsonPath("$.data.pageInfo.currentPage").value(0))
                .andExpect(jsonPath("$.data.products").isNotEmpty())
                .andExpect(jsonPath("$.data.products").isArray());
    }
}