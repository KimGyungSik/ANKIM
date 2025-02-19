package shoppingmall.ankim.docs.termsHistory;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import shoppingmall.ankim.docs.RestDocsSupport;
import shoppingmall.ankim.domain.member.controller.MemberEditApiController;
import shoppingmall.ankim.domain.member.controller.request.ChangePasswordRequest;
import shoppingmall.ankim.domain.member.controller.request.PasswordRequest;
import shoppingmall.ankim.domain.member.service.MemberEditService;
import shoppingmall.ankim.domain.security.helper.SecurityContextHelper;
import shoppingmall.ankim.domain.termsHistory.controller.TermsHistoryController;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsUpdateRequest;
import shoppingmall.ankim.domain.termsHistory.dto.TermsHistoryUpdateResponse;
import shoppingmall.ankim.domain.termsHistory.service.TermsHistoryService;
import shoppingmall.ankim.global.handler.LogoutHandler;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TermsHistoryControllerDocsTest extends RestDocsSupport {

    private final TermsHistoryService termsHistoryService = mock(TermsHistoryService.class);
    private final SecurityContextHelper securityContextHelper = mock(SecurityContextHelper.class);

    private static final String ACCESS_TOKEN = "example-access-token";
    private static final String REFRESH_TOKEN = "example-refresh-token";

    @Override
    protected Object initController() {
        return new TermsHistoryController(termsHistoryService, securityContextHelper);
    }

    @DisplayName("약관 변경(동의 및 철회) API")
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    @Test
    public void termsAgree() throws Exception {
        // given
        String loginId = "test@example.com";

        List<TermsUpdateRequest> request = List.of(
                new TermsUpdateRequest(4L, 1L, "Y"),
                new TermsUpdateRequest(6L, 2L, "N"),
                new TermsUpdateRequest(7L, 3L, "Y")
        );

        LocalDateTime now = LocalDateTime.now();
        TermsHistoryUpdateResponse response = TermsHistoryUpdateResponse.of(
                List.of("마케팅 목적의 개인정보 수집 및 이용 동의 동의가 완료되었습니다."
                        , "문자 수신 동의 거부가 완료되었습니다."
                        , "이메일 수신 동의 동의가 완료되었습니다.")
                , now);

        given(securityContextHelper.getLoginId()).willReturn(loginId);
        given(termsHistoryService.updateTermsAgreement(eq(loginId), anyList())).willReturn(response);


        // when & then
        mockMvc.perform(post("/api/terms/update")
                        .header("access", ACCESS_TOKEN)
                        .cookie(new Cookie("refresh", REFRESH_TOKEN))
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("member-terms-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("[].terms_no").type(JsonFieldType.NUMBER)
                                        .description("약관 번호"),
                                fieldWithPath("[].terms_hist_no").type(JsonFieldType.NUMBER).optional()
                                        .description("회원의 약관 변경 이력 번호. 이전에 동의한 이력이 없는 경우 null일 수 있음"),
                                fieldWithPath("[].terms_hist_agreeYn").type(JsonFieldType.STRING)
                                        .description("'Y' 또는 'N'으로 약관 동의 여부를 나타냄 ('Y': 동의, 'N': 철회)")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("응답 상태").type(JsonFieldType.STRING),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors").description("필드 오류 목록").optional().type(JsonFieldType.ARRAY),
                                fieldWithPath("data.message").description("응답 메시지 배열").type(JsonFieldType.ARRAY),
                                fieldWithPath("data.date").description("응답 날짜").type(JsonFieldType.STRING),
                                fieldWithPath("data.sender").description("응답 발신자 정보").type(JsonFieldType.STRING)
                        )
                ));
    }
}
