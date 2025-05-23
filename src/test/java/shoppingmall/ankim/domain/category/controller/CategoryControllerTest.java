package shoppingmall.ankim.domain.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import shoppingmall.ankim.domain.category.controller.request.CategoryCreateRequest;
import shoppingmall.ankim.domain.category.controller.request.CategoryUpdateRequest;
import shoppingmall.ankim.domain.category.dto.CategoryResponse;
import shoppingmall.ankim.domain.category.service.CategoryService;
import shoppingmall.ankim.domain.category.service.query.CategoryQueryService;
import shoppingmall.ankim.domain.image.service.S3Service;
import shoppingmall.ankim.global.config.JpaAuditingConfig;
import shoppingmall.ankim.global.config.QuerydslConfig;

import java.util.List;

import static org.mockito.BDDMockito.doNothing;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = CategoryController.class)
@TestPropertySource(properties = "spring.sql.init.mode=never")
@AutoConfigureMockMvc(addFilters = false) // CSRF 비활성화
@ImportAutoConfiguration(exclude =  {QuerydslConfig.class, JpaAuditingConfig.class})
class CategoryControllerTest {

    @MockBean
    private S3Service s3Service;

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
        CategoryCreateRequest request = CategoryCreateRequest.builder()
                .build();

        // when // then
        mockMvc.perform(
                        post("/category/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest()) // 응답 HTTP 상태 코드 검증
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.fieldErrors[0].reason").value("카테고리 이름은 필수 입력 값입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("모든 카테고리를 조회할 수 있다")
    @Test
    void getTotalCategories() throws Exception{
        // given
        List<CategoryResponse> result = List.of();

        given(categoryQueryService.fetchAllMiddleCategoriesWithSubCategories()).willReturn(result);

        // when // then
        mockMvc.perform(
                        get("/category/total")
                )
                .andDo(print())
                .andExpect(status().isOk()) // 응답 HTTP 상태 코드 검증
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @DisplayName("중분류와 소분류를 삭제할 수 있다")
    @Test
    void deleteCategory() throws Exception {
        // given
        Long categoryId = 1L;
        doNothing().when(categoryService).deleteCategory(categoryId);

        // when // then
        mockMvc.perform(
                        delete("/category/{categoryId}", categoryId)
                )
                .andDo(print())
                .andExpect(status().isOk()) // 응답 HTTP 상태 코드 검증
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("특정 중분류에 속한 모든 소분류를 조회할 수 있다")
    @Test
    void searchSubCategoriesUnderMiddleCategory() throws Exception {
        // given
        Long middleCategoryId = 1L;
        List<CategoryResponse> subCategories = List.of();
        given(categoryQueryService.getSubCategoriesUnderMiddleCategory(middleCategoryId)).willReturn(subCategories);

        // when // then
        mockMvc.perform(
                        get("/category/subcategories")
                                .param("middleCategoryId", String.valueOf(middleCategoryId))
                )
                .andDo(print())
                .andExpect(status().isOk()) // 응답 HTTP 상태 코드 검증
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @DisplayName("소분류 ID로 해당 소분류의 상위 중분류를 조회할 수 있다")
    @Test
    void findMiddleCategoryForSubCategory() throws Exception {
        // given
        Long subCategoryId = 2L;
        CategoryResponse parentCategoryResponse = new CategoryResponse(); // 예시 응답 데이터로 초기화
        given(categoryQueryService.findMiddleCategoryForSubCategory(subCategoryId)).willReturn(parentCategoryResponse);

        // when // then
        mockMvc.perform(
                        get("/category/parent")
                                .param("subCategoryId", String.valueOf(subCategoryId))
                )
                .andDo(print())
                .andExpect(status().isOk()) // 응답 HTTP 상태 코드 검증
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @DisplayName("중분류만 조회할 수 있다.")
    @Test
    void getMiddleCategories() throws Exception {
        // given
        List<CategoryResponse> middleCategories = List.of();
        given(categoryQueryService.retrieveMiddleCategories()).willReturn(middleCategories);

        // when // then
        mockMvc.perform(
                        get("/category/middle")
                )
                .andDo(print())
                .andExpect(status().isOk()) // 응답 HTTP 상태 코드 검증
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @DisplayName("카테고리 중분류를 수정할 수 있다.")
    @Test
    void updateMiddleCategory() throws Exception{
        // given
        Long categoryId = 1L;
        CategoryUpdateRequest request = CategoryUpdateRequest.builder()
                .name("새로운 중분류 이름")
                .build();
        doNothing().when(categoryService).updateMiddleCategory(categoryId, request.toServiceRequest());

        // when // then
        mockMvc.perform(
                        put("/category/middle/{id}", categoryId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("카테고리 소분류를 수정할 수 있다.")
    @Test
    void updateSubCategory() throws Exception {
        // given
        Long categoryId = 2L;
        CategoryUpdateRequest request = CategoryUpdateRequest.builder()
                .name("새로운 소분류 이름")
                .build();

        doNothing().when(categoryService).updateSubCategory(categoryId, request.toServiceRequest());

        // when // then
        mockMvc.perform(
                        put("/category/sub/{id}", categoryId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("카테고리 소분류의 부모(중분류)를 변경할 수 있다.")
    @Test
    void updateSubCategoryChangeMiddleCategory() throws Exception {
        // given
        Long categoryId = 3L;
        CategoryUpdateRequest request = CategoryUpdateRequest.builder()
                .name("소분류 이름")
                .newParentNo(1L) // 새로운 중분류의 ID
                .build();

        doNothing().when(categoryService).updateSubCategory(categoryId, request.toServiceRequest());

        // when // then
        mockMvc.perform(
                        put("/category/sub/{id}", categoryId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}