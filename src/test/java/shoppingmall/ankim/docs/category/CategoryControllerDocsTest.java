package shoppingmall.ankim.docs.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import shoppingmall.ankim.docs.RestDocsSupport;
import shoppingmall.ankim.domain.category.controller.CategoryController;
import shoppingmall.ankim.domain.category.controller.request.CategoryCreateRequest;
import shoppingmall.ankim.domain.category.dto.CategoryResponse;
import shoppingmall.ankim.domain.category.service.CategoryService;
import shoppingmall.ankim.domain.category.service.query.CategoryQueryService;
import shoppingmall.ankim.domain.category.service.request.CategoryCreateServiceRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shoppingmall.ankim.domain.category.entity.CategoryLevel.MIDDLE;

public class CategoryControllerDocsTest extends RestDocsSupport {

    private final CategoryService categoryService = mock(CategoryService.class);
    private final CategoryQueryService categoryQueryService = mock(CategoryQueryService.class);

    @Override
    protected Object initController() {
        return new CategoryController(categoryService,categoryQueryService);
    }

    @DisplayName("새로운 카테고리(중분류, 소분류)를 등록한다")
    @Test
    void createCategory() throws Exception {
        // given
        CategoryCreateRequest request = CategoryCreateRequest.builder()
                .name("상의")
                .build();

        given(categoryService.createCategory(any(CategoryCreateServiceRequest.class))).willReturn(
                CategoryResponse.builder()
                        .categoryNo(1L)
                        .parentNo(null)
                        .level(MIDDLE)
                        .name("상의")
                        .childCategories(null)
                        .build()
        );

        mockMvc.perform(
                        post("/category/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("category-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name").description("카테고리 이름").type(JsonFieldType.STRING),
                                fieldWithPath("parentNo").description("중분류일 경우 null, 소분류일 경우 상위 카테고리 ID").optional().type(JsonFieldType.NUMBER),
                                fieldWithPath("childCategories").description("중분류와 소분류를 동시에 추가할 경우 하위 카테고리 목록").optional().type(JsonFieldType.ARRAY)
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("응답 상태").type(JsonFieldType.STRING),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors").description("필드 오류 목록").optional().type(JsonFieldType.ARRAY),
                                fieldWithPath("data.categoryNo").description("카테고리 번호").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.parentNo").description("상위 카테고리 번호 (없으면 null)").optional().type(JsonFieldType.NUMBER),
                                fieldWithPath("data.level").description("카테고리 레벨").type(JsonFieldType.STRING),
                                fieldWithPath("data.name").description("카테고리 이름").type(JsonFieldType.STRING),
                                fieldWithPath("data.childCategories").description("하위 카테고리 목록 (없으면 빈 배열)").optional().type(JsonFieldType.ARRAY)
                        )));
    }
}
