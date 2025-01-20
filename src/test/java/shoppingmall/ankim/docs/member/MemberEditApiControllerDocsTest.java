package shoppingmall.ankim.docs.member;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import shoppingmall.ankim.docs.RestDocsSupport;
import shoppingmall.ankim.domain.member.controller.MemberEditApiController;
import shoppingmall.ankim.domain.member.controller.request.ChangePasswordRequest;
import shoppingmall.ankim.domain.member.controller.request.PasswordRequest;
import shoppingmall.ankim.domain.member.service.MemberEditService;
import shoppingmall.ankim.domain.security.helper.SecurityContextHelper;
import shoppingmall.ankim.global.handler.LogoutHandler;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MemberEditApiControllerDocsTest extends RestDocsSupport {

    private final MemberEditService memberEditService = mock(MemberEditService.class);
    private final SecurityContextHelper securityContextHelper = mock(SecurityContextHelper.class);
    private final LogoutHandler logoutHandler = mock(LogoutHandler.class);

    private static final String ACCESS_TOKEN = "example-access-token";
    private static final String REFRESH_TOKEN = "example-refresh-token";

    @Override
    protected Object initController() {
        return new MemberEditApiController(memberEditService, securityContextHelper, logoutHandler);
    }

    @DisplayName("비밀번호 검증 API")
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    @Test
    public void confirmPassword() throws Exception {
        // given
        String loginId = "test@example.com";
        String password = "password!123";
        PasswordRequest request = PasswordRequest.builder()
                .password(password)
                .build();

        given(securityContextHelper.getLoginId()).willReturn(loginId);

        // when & then
        mockMvc.perform(post("/api/edit/confirm-password")
                        .header("access", ACCESS_TOKEN)
                        .cookie(new Cookie("refresh", REFRESH_TOKEN))
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("member-confirm-password",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("password").type(JsonFieldType.STRING).description("확인할 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("응답 상태").type(JsonFieldType.STRING),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors").description("필드 오류 목록").optional().type(JsonFieldType.ARRAY),
                                fieldWithPath("data").description("응답 데이터").type(JsonFieldType.STRING),
                                fieldWithPath("jwtError").description("JWT 인증 오류 여부").type(JsonFieldType.BOOLEAN).optional()
                        )
                ));
    }

    @DisplayName("비밀번호 변경 API")
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    @Test
    public void changePassword() throws Exception {
        // given
        String loginId = "test@example.com";
        String oldPassword = "password!123";
        String newPassword = "newPassword!123";
        String confirmPassword = "newPassword!123";
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .oldPassword(oldPassword)
                .newPassword(newPassword)
                .confirmPassword(confirmPassword)
                .build();

        given(securityContextHelper.getLoginId()).willReturn(loginId);

        // when & then
        mockMvc.perform(put("/api/edit/change-password")
                        .header("access", ACCESS_TOKEN)
                        .cookie(new Cookie("refresh", REFRESH_TOKEN))
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("member-change-password",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("oldPassword").type(JsonFieldType.STRING).description("사용자가 현재 사용 중인 기존 비밀번호"),
                                fieldWithPath("newPassword").type(JsonFieldType.STRING).description("새로 설정할 비밀번호"),
                                fieldWithPath("confirmPassword").type(JsonFieldType.STRING).description("새 비밀번호와 동일한지 확인하기 위한 확인 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("응답 상태").type(JsonFieldType.STRING),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors").description("필드 오류 목록").optional().type(JsonFieldType.ARRAY),
                                fieldWithPath("data").description("응답 데이터").type(JsonFieldType.STRING),
                                fieldWithPath("jwtError").description("JWT 인증 오류 여부").type(JsonFieldType.BOOLEAN).optional()
                        )
                ));
    }
}
