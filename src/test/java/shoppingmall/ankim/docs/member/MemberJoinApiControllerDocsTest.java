package shoppingmall.ankim.docs.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import shoppingmall.ankim.docs.RestDocsSupport;
import shoppingmall.ankim.domain.category.dto.CategoryResponse;
import shoppingmall.ankim.domain.category.service.request.CategoryCreateServiceRequest;
import shoppingmall.ankim.domain.member.controller.MemberJoinApiController;
import shoppingmall.ankim.domain.member.controller.request.MemberEmailRequest;
import shoppingmall.ankim.domain.member.controller.request.MemberRegisterRequest;
import shoppingmall.ankim.domain.member.dto.MemberResponse;
import shoppingmall.ankim.domain.member.service.MemberService;
import shoppingmall.ankim.domain.member.service.request.MemberRegisterServiceRequest;
import shoppingmall.ankim.domain.terms.service.query.TermsQueryService;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsAgreement;

import java.time.LocalDate;
import java.util.List;

import static org.awaitility.Awaitility.given;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shoppingmall.ankim.domain.category.entity.CategoryLevel.MIDDLE;

public class MemberJoinApiControllerDocsTest extends RestDocsSupport {

    private final MemberService memberService = mock(MemberService.class);
    private final TermsQueryService termsQueryService = mock(TermsQueryService.class);

    @Override
    protected Object initController() {
        return new MemberJoinApiController(memberService, termsQueryService);
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
        // given
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

    @DisplayName("회원가입 정보 입력 API")
    @Test
    public void register() throws Exception {
        // given
        MemberRegisterRequest request = MemberRegisterRequest.builder()
                .loginId("test@example.com")
                .password("testPassword!123")
                .name("홍길동")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1999, 12, 19))
                .gender("F")
                .build();

        // 세션 데이터 설정
        MockHttpSession session = new MockHttpSession();
        List<TermsAgreement> termsAgreements = List.of(
                TermsAgreement.builder()
                        .no(1L)
                        .name("만 14세 이상입니다")
                        .agreeYn("Y")
                        .level(2)
                        .termsYn("Y")
                        .build(),
                TermsAgreement.builder()
                        .no(2L)
                        .name("이용약관 동의")
                        .agreeYn("Y")
                        .level(2)
                        .termsYn("Y")
                        .build()
        );
        session.setAttribute("termsAgreements", termsAgreements);

        given(memberService.registerMember(any(MemberRegisterServiceRequest.class), anyList()))
                .willReturn(MemberResponse.builder()
                        .no(1L)
                        .name("홍*동")
                        .build());

        // when, then
        mockMvc.perform(post("/api/member/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .session(session)
                )
                .andExpect(status().isOk())
                .andDo(document("member-register",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("loginId").type(JsonFieldType.STRING)
                                        .description("이메일 아이디"),
                                fieldWithPath("password").type(JsonFieldType.STRING)
                                        .description("비밀번호"),
                                fieldWithPath("name").type(JsonFieldType.STRING)
                                        .description("이름"),
                                fieldWithPath("phoneNum").type(JsonFieldType.STRING)
                                        .description("휴대전화번호"),
                                fieldWithPath("birth").type(JsonFieldType.ARRAY)
                                        .description("생년월일"),
                                fieldWithPath("gender").type(JsonFieldType.STRING)
                                        .description("성별")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("응답 상태").type(JsonFieldType.STRING),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors").description("필드 오류 목록").optional().type(JsonFieldType.ARRAY),
                                fieldWithPath("data.no").description("회원 번호").type(JsonFieldType.NUMBER),
                                fieldWithPath("data.name").description("마스킹 처리된 회원 이름").type(JsonFieldType.STRING)
                        )
                ));

    }
}
