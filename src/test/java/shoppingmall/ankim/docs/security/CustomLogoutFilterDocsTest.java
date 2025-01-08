package shoppingmall.ankim.docs.security;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import shoppingmall.ankim.docs.RestDocsSupport;
import shoppingmall.ankim.domain.security.controller.ReissueController;
import shoppingmall.ankim.domain.security.filter.CustomLogoutFilter;
import shoppingmall.ankim.domain.security.handler.RedisHandler;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;
import shoppingmall.ankim.domain.security.service.ReissueService;

import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
public class CustomLogoutFilterDocsTest {

    private MockMvc mockMvc;
    private final JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
    private final RedisHandler redisHandler = mock(RedisHandler.class);

    private static final String ACCESS_TOKEN = "example-access-token";
    private static final String REFRESH_TOKEN = "example-refresh-token";

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.standaloneSetup(new Object())
                .addFilters(new CustomLogoutFilter(jwtTokenProvider, redisHandler))
                .apply(documentationConfiguration(restDocumentation)) // REST Docs 설정
                .alwaysDo(document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())))
                .build();
    }

    @DisplayName("로그아웃 API")
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    @Test
    public void logout() throws Exception {
        // given
        // Mock 설정
        given(redisHandler.get(ACCESS_TOKEN)).willReturn(REFRESH_TOKEN);
        given(jwtTokenProvider.getCategoryFromToken(REFRESH_TOKEN)).willReturn("refresh");
        given(jwtTokenProvider.isTokenExpired(REFRESH_TOKEN)).willReturn(false);

        // when & then
        mockMvc.perform(post("/logout")
                        .header("access", ACCESS_TOKEN)
                        .cookie(new Cookie("refresh", REFRESH_TOKEN))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("logout",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("응답 상태").type(JsonFieldType.STRING),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors").description("필드 오류 목록").optional().type(JsonFieldType.ARRAY),
                                fieldWithPath("data").description("응답 데이터").optional().type(JsonFieldType.NULL)
                        )
                ));
    }


}
