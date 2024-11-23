package shoppingmall.ankim.domain.product.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.domain.product.dto.ProductResponse;
import shoppingmall.ankim.domain.product.dto.ProductUserDetailResponse;
import shoppingmall.ankim.domain.product.repository.ProductRepository;

import static org.mockito.ArgumentMatchers.anyLong;
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
}