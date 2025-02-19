package shoppingmall.ankim.docs.mail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.test.web.servlet.ResultActions;
import shoppingmall.ankim.docs.RestDocsSupport;
import shoppingmall.ankim.domain.email.controller.MailApiController;
import shoppingmall.ankim.domain.email.controller.request.MailRequest;
import shoppingmall.ankim.domain.email.service.Count;
import shoppingmall.ankim.domain.email.service.MailService;
import shoppingmall.ankim.domain.member.controller.MemberJoinApiController;
import shoppingmall.ankim.domain.member.service.MemberService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MailApiControllerDocsTest extends RestDocsSupport {

    private final MailService mailService = mock(MailService.class);

    @Override
    protected Object initController() {
        return new MailApiController(mailService);
    }

    @DisplayName("이메일 인증 요청 API")
    @Test
    public void sendMail() throws Exception {
        // given
        Mockito.doAnswer(invocation -> null).when(mailService).sendMail(any());
        String loginId = "test@example.com";

        // when & then
        mockMvc.perform(post("/api/mail/send") // 경로 수정
                        .queryParam("loginId", loginId) // 요청 쿼리 파라미터 추가
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("mail-send",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("loginId").description("이메일 형식의 로그인 ID")
                        ),
                        responseFields( // 응답 필드 문서화
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("응답 상태").type(JsonFieldType.STRING),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors").description("필드 오류 목록").optional().type(JsonFieldType.ARRAY),
                                fieldWithPath("data").description("응답 데이터"),
                                fieldWithPath("jwtError").description("JWT 인증 오류 여부").type(JsonFieldType.BOOLEAN).optional()
                        )
                ));
    }

    @DisplayName("인증번호 검증 요청 API")
    @Test
    public void verifyMail() throws Exception {
        // given
        Mockito.when(mailService.verifyCode(eq("test@example.com"), eq("7WqnR5"))).thenReturn(Count.SUCCESS);

        MailRequest request = MailRequest.builder()
                .loginId("test@example.com")
                .verificationCode("7WqnR5")
                .build();

        // when & then
        mockMvc.perform(post("/api/mail/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andDo(document("mail-verify",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("loginId").type(JsonFieldType.STRING)
                                .description("사용할 이메일 아이디"),
                                fieldWithPath("verificationCode").type(JsonFieldType.STRING)
                                        .description("이메일 인증 코드")),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("응답 상태").type(JsonFieldType.STRING),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors").description("필드 오류 목록").optional().type(JsonFieldType.ARRAY),
                                fieldWithPath("data").description("응답 데이터").optional().type(JsonFieldType.STRING),
                                fieldWithPath("jwtError").description("JWT 인증 오류 여부").type(JsonFieldType.BOOLEAN).optional()
                        )
                ));
    }
}
