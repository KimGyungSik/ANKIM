package shoppingmall.ankim.docs.login;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import shoppingmall.ankim.docs.RestDocsSupport;
import shoppingmall.ankim.domain.login.controller.LoginApiController;
import shoppingmall.ankim.domain.login.controller.request.AdminLoginRequest;
import shoppingmall.ankim.domain.login.controller.request.MemberLoginRequest;
import shoppingmall.ankim.domain.login.service.LoginService;

import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LoginApiControllerDocsTest extends RestDocsSupport {

    private final LoginService loginService = mock(LoginService.class);

    @Override
    protected Object initController() {
        return new LoginApiController(loginService);
    }


    @DisplayName("회원 로그인 API")
    @Test
    public void memberLogin() throws Exception {
        // given
        MemberLoginRequest memberLoginRequest = MemberLoginRequest.builder()
                .loginId("test@example.com")
                .password("password123")
                .autoLogin("rememberMe")
                .build();

        Map<String, Object> tokenResponse = Map.of(
                "access", "access-token-example",
                "refresh", "refresh-token-example",
                "expireTime", 604800000L // 7일
        );

        given(loginService.memberLogin(any(), any())).willReturn(tokenResponse);

        // when & then
        mockMvc.perform(post("/api/login/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberLoginRequest)))
                .andExpect(status().isOk())
                .andDo(document("login-member",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("loginId").type(JsonFieldType.STRING)
                                        .description("회원의 이메일 ID"),
                                fieldWithPath("password").type(JsonFieldType.STRING)
                                        .description("회원의 비밀번호"),
                                fieldWithPath("loginType").optional().type(JsonFieldType.STRING)
                                        .description("로그인 타입"),
                                fieldWithPath("loginTime").optional().type(JsonFieldType.ARRAY)
                                        .description("로그인 시각"),
                                fieldWithPath("autoLogin").optional().type(JsonFieldType.STRING)
                                        .description("자동 로그인 여부( 자동 로그인 : \"rememberMe\" ")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("응답 상태").type(JsonFieldType.STRING),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors").description("필드 오류 목록").optional().type(JsonFieldType.ARRAY),
                                fieldWithPath("data").description("로그인 성공 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("jwtError").description("JWT 인증 오류 여부").type(JsonFieldType.BOOLEAN).optional()
                        ),
                        responseHeaders(
                                headerWithName("access").description("Access 토큰 (헤더에 포함)"),
                                headerWithName("Set-Cookie").description("Refresh 토큰 (쿠키에 포함)")
                        )
                ));
    }

    @DisplayName("관리자 로그인 API")
    @Test
    public void adminLogin() throws Exception {
        // given
        AdminLoginRequest adminLoginRequest = AdminLoginRequest.builder()
                .loginId("admin")
                .password("adminPass123")
                .build();

        Map<String, Object> tokenResponse = Map.of(
                "access", "admin-access-token-example",
                "refresh", "admin-refresh-token-example",
                "expireTime", 3600000L // 1시간
        );

        when(loginService.adminLogin(any(), any())).thenReturn(tokenResponse);

        // when & then
        mockMvc.perform(post("/api/login/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLoginRequest)))
                .andExpect(status().isOk())
                .andDo(document("login-admin",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("loginId").type(JsonFieldType.STRING)
                                        .description("관리자의 이메일 ID"),
                                fieldWithPath("password").type(JsonFieldType.STRING)
                                        .description("관리자의 비밀번호"),
                                fieldWithPath("loginType").optional().type(JsonFieldType.STRING)
                                        .description("로그인 타입"),
                                fieldWithPath("loginTime").optional().type(JsonFieldType.ARRAY)
                                        .description("로그인 시각"),
                                fieldWithPath("autoLogin").optional().type(JsonFieldType.STRING)
                                        .description("자동 로그인 여부( 자동 로그인 : \"rememberMe\" ")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("응답 상태").type(JsonFieldType.STRING),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors").description("필드 오류 목록").optional().type(JsonFieldType.ARRAY),
                                fieldWithPath("data").description("로그인 성공 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("jwtError").description("JWT 인증 오류 여부").type(JsonFieldType.BOOLEAN).optional()
                        ),
                        responseHeaders(
                                headerWithName("access").description("Access 토큰 (헤더에 포함)"),
                                headerWithName("Set-Cookie").description("Refresh 토큰 (쿠키에 포함)")
                        )
                ));
    }
}
