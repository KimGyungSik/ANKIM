package shoppingmall.ankim.docs.admin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.payload.JsonFieldType;
import shoppingmall.ankim.docs.RestDocsSupport;
import shoppingmall.ankim.domain.admin.controller.AdminApiController;
import shoppingmall.ankim.domain.admin.controller.request.AdminIdValidRequest;
import shoppingmall.ankim.domain.admin.controller.request.AdminRegisterRequest;
import shoppingmall.ankim.domain.admin.service.AdminService;
import shoppingmall.ankim.domain.member.controller.MemberJoinApiController;
import shoppingmall.ankim.domain.member.controller.request.MemberEmailRequest;
import shoppingmall.ankim.domain.member.controller.request.MemberRegisterRequest;
import shoppingmall.ankim.domain.member.dto.MemberResponse;
import shoppingmall.ankim.domain.member.service.MemberService;
import shoppingmall.ankim.domain.member.service.request.MemberRegisterServiceRequest;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsAgreement;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminApiControllerDocsTest extends RestDocsSupport {

    private final AdminService adminService = mock(AdminService.class);

    @Override
    protected Object initController() {
        return new AdminApiController(adminService);
    }

    @DisplayName("아이디 중복 체크 API")
    @Test
    public void checkLoginId() throws Exception {
        // given
        AdminIdValidRequest request = AdminIdValidRequest.builder()
                .loginId("admin2412")
                .build();

        // when, then
        mockMvc.perform(post("/api/admin/check-login-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andDo(document("admin-login-id-check",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(fieldWithPath("loginId").type(JsonFieldType.STRING)
                                .description("사용할 관리자 아이디")),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("응답 상태").type(JsonFieldType.STRING),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors").description("필드 오류 목록").optional().type(JsonFieldType.ARRAY),
                                fieldWithPath("data").description("응답 데이터").type(JsonFieldType.STRING)
                        )
                ));
    }

    @DisplayName("회원가입 정보 입력 API")
    @Test
    public void register() throws Exception {
        // given
        AdminRegisterRequest request = AdminRegisterRequest.builder()
                .loginId("admin2412")
                .password("AdminPass123!")
                .name("홍길동")
                .email("admin2412@ankim.com")
                .phoneNum("010-1234-5678")
                .officeNum("02-9876-5432")
                .birth(LocalDate.of(1993, 3, 3))
                .gender("M")
                .joinDate(LocalDate.of(2024, 7, 1))
                .zipCode(12345)
                .addressMain("서울특별시 송파구 올림픽로 35길 125")
                .addressDetail("삼성SDS")
                .build();

        // when, then
        mockMvc.perform(post("/api/admin/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andDo(document("admin-register",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("loginId").type(JsonFieldType.STRING)
                                        .description("아이디"),
                                fieldWithPath("password").type(JsonFieldType.STRING)
                                        .description("비밀번호"),
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("이름"),
                                fieldWithPath("email").type(JsonFieldType.STRING)
                                        .description("회사 이메일"),
                                fieldWithPath("phoneNum").type(JsonFieldType.STRING)
                                        .description("개인 휴대전화번호"),
                                fieldWithPath("officeNum").type(JsonFieldType.STRING).optional()
                                        .description("사무실 유선전화번호"),
                                fieldWithPath("birth").type(JsonFieldType.ARRAY)
                                        .description("생년월일"),
                                fieldWithPath("gender").type(JsonFieldType.STRING)
                                        .description("성별"),
                                fieldWithPath("joinDate").type(JsonFieldType.ARRAY).optional()
                                        .description("입사일"),
                                fieldWithPath("status").type(JsonFieldType.STRING).optional()
                                        .description("관리자 상태"),
                                fieldWithPath("zipCode").type(JsonFieldType.NUMBER)
                                        .description("우편번호"),
                                fieldWithPath("addressMain").type(JsonFieldType.STRING)
                                        .description("기본 주소"),
                                fieldWithPath("addressDetail").type(JsonFieldType.STRING).optional()
                                        .description("상세 주소")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("응답 상태").type(JsonFieldType.STRING),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors").description("필드 오류 목록").optional().type(JsonFieldType.ARRAY),
                                fieldWithPath("data").description("응답 데이터").type(JsonFieldType.STRING)
                        )
                ));

    }
}
