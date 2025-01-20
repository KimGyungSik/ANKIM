package shoppingmall.ankim.docs.memberLeave;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import shoppingmall.ankim.docs.RestDocsSupport;
import shoppingmall.ankim.domain.member.controller.MemberJoinApiController;
import shoppingmall.ankim.domain.member.controller.request.MemberEmailRequest;
import shoppingmall.ankim.domain.member.controller.request.MemberRegisterRequest;
import shoppingmall.ankim.domain.member.dto.MemberResponse;
import shoppingmall.ankim.domain.member.service.MemberService;
import shoppingmall.ankim.domain.member.service.request.MemberRegisterServiceRequest;
import shoppingmall.ankim.domain.memberLeave.controller.MemberLeaveController;
import shoppingmall.ankim.domain.memberLeave.controller.request.LeaveRequest;
import shoppingmall.ankim.domain.memberLeave.service.MemberLeaveService;
import shoppingmall.ankim.domain.security.helper.SecurityContextHelper;
import shoppingmall.ankim.domain.termsHistory.controller.request.TermsAgreement;
import shoppingmall.ankim.global.handler.LogoutHandler;
import shoppingmall.ankim.global.response.ApiResponse;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MemberLeaveControllerDocsTest extends RestDocsSupport {

    private final MemberLeaveService memberLeaveService = mock(MemberLeaveService.class);
    private final SecurityContextHelper securityContextHelper = mock(SecurityContextHelper.class);
    private final LogoutHandler logoutHandler = mock(LogoutHandler.class);
    

    @Override
    protected Object initController() {
        return new MemberLeaveController(memberLeaveService, securityContextHelper, logoutHandler);
    }

    @DisplayName("회원 탈퇴 API")
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    @Test
    public void leaveMember() throws Exception {
        // given
        String loginId = "test@example.com";

        LeaveRequest request = LeaveRequest.builder()
                .leaveReasonNo(6L)
                .leaveReason("기타")
                .leaveMessage("탈퇴 사유(사용자 작성)")
                .agreeYn("Y")
                .password("password!123")
                .build();

        given(securityContextHelper.getLoginId()).willReturn(loginId);

        // when & then
        mockMvc.perform(post("/api/leave/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("member-leave",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("leaveReasonNo").description("선택한 탈퇴 사유 ID").type(JsonFieldType.NUMBER),
                                fieldWithPath("leaveReason").description("선택한 탈퇴 사유명").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("leaveMessage").description("기타 사유(사용자 작성)").type(JsonFieldType.STRING).optional(),
                                fieldWithPath("agreeYn").description("탈퇴 동의 여부(Y/N)").type(JsonFieldType.STRING),
                                fieldWithPath("password").description("비밀번호 검증용").type(JsonFieldType.STRING)
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드").type(JsonFieldType.NUMBER),
                                fieldWithPath("status").description("응답 상태").type(JsonFieldType.STRING),
                                fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                fieldWithPath("fieldErrors").description("필드 오류 목록").type(JsonFieldType.ARRAY).optional(),
                                fieldWithPath("data").description("응답 데이터").type(JsonFieldType.STRING),
                                fieldWithPath("jwtError").description("JWT 인증 오류 여부").type(JsonFieldType.BOOLEAN).optional()
                        )
                ));
    }
}
