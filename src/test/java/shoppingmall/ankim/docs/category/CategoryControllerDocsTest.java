package shoppingmall.ankim.docs.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import shoppingmall.ankim.docs.RestDocsSupport;
import shoppingmall.ankim.domain.category.controller.CategoryController;
import shoppingmall.ankim.domain.category.controller.request.CategoryCreateRequest;
import shoppingmall.ankim.domain.category.controller.request.CategoryUpdateRequest;
import shoppingmall.ankim.domain.category.dto.CategoryResponse;
import shoppingmall.ankim.domain.category.exception.CategoryLinkedWithProductException;
import shoppingmall.ankim.domain.category.exception.ChildCategoryExistsException;
import shoppingmall.ankim.domain.category.service.CategoryService;
import shoppingmall.ankim.domain.category.service.query.CategoryQueryService;
import shoppingmall.ankim.domain.category.service.request.CategoryCreateServiceRequest;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shoppingmall.ankim.domain.category.entity.CategoryLevel.MIDDLE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static shoppingmall.ankim.global.exception.ErrorCode.CATEGORY_LINKED_WITH_PRODUCT;
import static shoppingmall.ankim.global.exception.ErrorCode.CHILD_CATEGORY_EXISTS;

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

    @DisplayName("카테고리 중분류를 수정한다")
    @Test
    void updateMiddleCategory() throws Exception {
        // given
        Long categoryId = 1L;
        CategoryUpdateRequest request = CategoryUpdateRequest.builder()
                .name("새로운 중분류 이름")
                .build();

        // when
        mockMvc.perform(
                        RestDocumentationRequestBuilders.put("/category/middle/{id}", categoryId)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("category-update-middle",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("수정할 중분류 카테고리 ID")
                        ),
                        requestFields(
                                fieldWithPath("name").description("수정할 카테고리 이름").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("newParentNo").description("새로운 부모 카테고리 ID (중분류 수정 시 null)").optional().type(JsonFieldType.NUMBER)
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("응답 상태").type(JsonFieldType.STRING),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors").description("필드 오류 목록").optional().type(JsonFieldType.ARRAY),
                                fieldWithPath("data").description("응답 데이터 (없음)").optional().type(JsonFieldType.NULL)
                        )));
    }

    @DisplayName("카테고리 소분류의 부모를 변경한다")
    @Test
    void updateSubCategoryChangeParent() throws Exception {
        // given
        Long categoryId = 2L;
        CategoryUpdateRequest request = CategoryUpdateRequest.builder()
                .name("변경된 소분류")
                .newParentNo(1L) // 새로운 중분류 ID
                .build();

        // when
        mockMvc.perform(
                        RestDocumentationRequestBuilders.put("/category/sub/{id}", categoryId)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("category-update-sub-change-parent",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("수정할 소분류 카테고리 ID")
                        ),
                        requestFields(
                                fieldWithPath("name").description("수정할 소분류 이름").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("newParentNo").description("새로운 부모 카테고리 ID (중분류 ID)").optional().type(JsonFieldType.NUMBER)
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("응답 상태").type(JsonFieldType.STRING),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors").description("필드 오류 목록").optional().type(JsonFieldType.ARRAY),
                                fieldWithPath("data").description("응답 데이터 (없음)").optional().type(JsonFieldType.NULL)
                        )));
    }

    @DisplayName("모든 카테고리를 조회한다")
    @Test
    void getAllCategories() throws Exception {
        // given
        List<CategoryResponse> categories = List.of();
        given(categoryQueryService.fetchAllMiddleCategoriesWithSubCategories()).willReturn(categories);

        // when
        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/category/total")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("category-get-all",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("응답 상태").type(JsonFieldType.STRING),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors").description("필드 오류 목록").optional().type(JsonFieldType.ARRAY),
                                fieldWithPath("data").description("모든 카테고리 목록").type(JsonFieldType.ARRAY)
                        )));
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
                        RestDocumentationRequestBuilders.put("/category/sub/{id}", categoryId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("category-update-sub",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("수정할 소분류 카테고리 ID")
                        ),
                        requestFields(
                                fieldWithPath("name").description("수정할 소분류 이름").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("newParentNo").description("새로운 부모 카테고리 ID (중분류 ID)").optional().type(JsonFieldType.NUMBER)
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("응답 상태").type(JsonFieldType.STRING),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors").description("필드 오류 목록").optional().type(JsonFieldType.ARRAY),
                                fieldWithPath("data").description("응답 데이터 (없음)").optional().type(JsonFieldType.NULL)
                        )));
    }

    @DisplayName("카테고리를 등록할 때 이름은 필수값이다.")
    @Test
    void createCategoryWithoutName() throws Exception {
        // given
        CategoryCreateRequest request = CategoryCreateRequest.builder().build();

        // when // then
        mockMvc.perform(
                        post("/category/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest()) // 검증 실패로 인해 400 응답 예상
                .andDo(document("category-create-missing-name",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("name").description("카테고리 이름 (필수)").type(JsonFieldType.NULL),
                                fieldWithPath("parentNo").description("중분류일 경우 null, 소분류일 경우 상위 카테고리 ID").optional().type(JsonFieldType.NUMBER),
                                fieldWithPath("childCategories").description("중분류와 소분류를 동시에 추가할 경우 하위 카테고리 목록").optional().type(JsonFieldType.ARRAY)
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("응답 상태").type(JsonFieldType.STRING),
                                fieldWithPath("message").description("예외 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors").description("필드 오류 목록").optional().type(JsonFieldType.ARRAY),
                                fieldWithPath("fieldErrors[].field").description("문제 발생 필드 이름").optional().type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors[].rejectedValue").description("거부된 값").optional().type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors[].reason").description("거부 사유").optional().type(JsonFieldType.STRING),
                                fieldWithPath("data").description("응답 데이터 (없음)").optional().type(JsonFieldType.NULL)
                        )));
    }


    @DisplayName("특정 중분류에 속한 모든 소분류를 조회할 수 있다.")
    @Test
    void searchSubCategoriesUnderMiddleCategory() throws Exception {
        // given
        Long middleCategoryId = 1L;
        List<CategoryResponse> subCategories = List.of();
        given(categoryQueryService.getSubCategoriesUnderMiddleCategory(middleCategoryId)).willReturn(subCategories);

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/category/subcategories")
                                .param("middleCategoryId", String.valueOf(middleCategoryId))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("category-get-subcategories-under-middle",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("middleCategoryId").description("중분류 카테고리 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("응답 상태").type(JsonFieldType.STRING),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors").description("필드 오류 목록").optional().type(JsonFieldType.ARRAY),
                                fieldWithPath("data").description("소분류 목록").type(JsonFieldType.ARRAY)
                        )));
    }


    @DisplayName("카테고리를 삭제할 수 있다.")
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
                .andExpect(status().isOk())
                .andDo(document("category-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("categoryId").description("삭제할 카테고리 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("응답 상태").type(JsonFieldType.STRING),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors").description("필드 오류 목록").optional().type(JsonFieldType.ARRAY),
                                fieldWithPath("data").description("응답 데이터 (없음)").optional().type(JsonFieldType.NULL)
                        )));
    }

    @DisplayName("삭제하고 싶은 카테고리에 상품이 존재할 경우 예외가 발생한다.")
    @Test
    void deleteCategoryLinkedWithProductException() throws Exception {
        // given
        Long categoryId = 1L;

        // 예외가 발생하도록 설정
        doThrow(new CategoryLinkedWithProductException(CATEGORY_LINKED_WITH_PRODUCT))
                .when(categoryService).deleteCategory(categoryId);

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/category/{categoryId}", categoryId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("category-delete-linked-with-product-exception",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("categoryId").description("삭제하려는 카테고리 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("응답 상태").type(JsonFieldType.STRING),
                                fieldWithPath("message").description("에러 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors").description("필드 오류 목록").optional().type(JsonFieldType.ARRAY),
                                fieldWithPath("data").description("응답 데이터").optional().type(JsonFieldType.NULL)
                        )
                ));
    }

    @DisplayName("중분류를 삭제하려고 할 때 소분류가 존재하면 예외가 발생한다.")
    @Test
    void deleteCategoryWithSubCategoriesException() throws Exception {
        // given
        Long categoryId = 2L;

        // 예외가 발생하도록 설정
        doThrow(new ChildCategoryExistsException(CHILD_CATEGORY_EXISTS))
                .when(categoryService).deleteCategory(categoryId);

        // when // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/category/{categoryId}", categoryId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isConflict()) // HttpStatus.CONFLICT
                .andDo(document("category-delete-child-category-exists-exception",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("categoryId").description("삭제하려는 카테고리 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("응답 상태").type(JsonFieldType.STRING),
                                fieldWithPath("message").description("에러 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors").description("필드 오류 목록").optional().type(JsonFieldType.ARRAY),
                                fieldWithPath("data").description("응답 데이터").optional().type(JsonFieldType.NULL)
                        )
                ));
    }


}
