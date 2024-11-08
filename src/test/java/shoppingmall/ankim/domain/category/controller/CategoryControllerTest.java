package shoppingmall.ankim.domain.category.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import shoppingmall.ankim.domain.category.controller.request.CategoryCreateRequest;
import shoppingmall.ankim.domain.category.service.CategoryService;
import shoppingmall.ankim.domain.category.service.query.CategoryQueryService;
import shoppingmall.ankim.global.config.JpaAuditingConfig;
import shoppingmall.ankim.global.config.QuerydslConfig;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoryController.class)
@ImportAutoConfiguration(exclude =  {QuerydslConfig.class, JpaAuditingConfig.class})
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private CategoryQueryService categoryQueryService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("새로운 카테고리를 등록한다")
    @Test
    void createCategory() throws Exception {
        // given
        CategoryCreateRequest request = CategoryCreateRequest.builder()
                .name("하의")
                .build();

        // when // then
        mockMvc.perform(
                        post("/category/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("카테고리를 등록할 때 이름은 필수값이다.")
    @Test
    void createCategoryWithoutName() throws Exception{
        // given

        // when

        // then
    }

    @DisplayName("모든 카테고리를 조회할 수 있다")
    @Test
    void getTotalCategories() throws Exception{
        // given

        // when

        // then
    }
}