package shoppingmall.ankim.docs.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import shoppingmall.ankim.docs.RestDocsSupport;
import shoppingmall.ankim.domain.member.controller.MemberJoinApiController;
import shoppingmall.ankim.domain.member.controller.request.MemberEmailRequest;
import shoppingmall.ankim.domain.member.service.MemberService;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MemberJoinApiControllerDocsTest extends RestDocsSupport {

    private final MemberService memberService = mock(MemberService.class);

    @Override
    protected Object initController() {
        return new MemberJoinApiController(memberService);
    }

    @DisplayName("회원가입을 위한 약관동의 내역을 전송하는 API")
    @Test
    public void termsAgreements() throws Exception {
        // given
        String request = """
        [
            {
                "no": 2,
                "name": "만 14세 이상입니다",
                "agreeYn": "Y",
                "level": 2,
                "termsYn": "Y"
            },
            {
                "no": 3,
                "name": "이용약관 동의",
                "agreeYn": "Y",
                "level": 2,
                "termsYn": "Y"
            },
            {
                "no": 5,
                "name": "광고성 정보 수신 동의",
                "agreeYn": "Y",
                "level": 3,
                "termsYn": "N"
            }
        ]
    """;

        // when, then
        mockMvc.perform(post("/api/member/terms-next")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andDo(document("member-terms-agreements",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("[].no").type(JsonFieldType.NUMBER)
                                        .description("약관 번호"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING)
                                        .description("약관 이름"),
                                fieldWithPath("[].agreeYn").type(JsonFieldType.STRING)
                                        .description("약관 동의 여부 (Y/N)"),
                                fieldWithPath("[].level").type(JsonFieldType.NUMBER)
                                        .description("약관 레벨"),
                                fieldWithPath("[].termsYn").type(JsonFieldType.STRING)
                                        .description("필수 약관 여부 (Y/N)")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("응답 상태").type(JsonFieldType.STRING),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors").description("필드 오류 목록").optional().type(JsonFieldType.ARRAY),
                                fieldWithPath("data").description("응답 데이터")

                        )
                ));

    }

    @DisplayName("사용가능한 이메일인지 검증하는 API")
    @Test
    public void existByEmail() throws Exception {

        MemberEmailRequest request = MemberEmailRequest.builder()
                .loginId("test@example.com")
                .build();

        // when, then
        mockMvc.perform(post("/api/member/email-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andDo(document("member-email-check",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(fieldWithPath("loginId").type(JsonFieldType.STRING)
                                .description("사용할 이메일 아이디")),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("응답 상태").type(JsonFieldType.STRING),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors").description("필드 오류 목록").optional().type(JsonFieldType.ARRAY),
                                fieldWithPath("data").description("응답 데이터")
                        )
                ));
    }
}
