package shoppingmall.ankim.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import shoppingmall.ankim.domain.member.controller.request.MemberEmailRequest;
import shoppingmall.ankim.domain.member.exception.MemberRegistrationException;
import shoppingmall.ankim.domain.member.service.MemberService;
import shoppingmall.ankim.domain.security.service.CustomUserDetailsService;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;
import shoppingmall.ankim.domain.terms.service.query.TermsQueryService;
import shoppingmall.ankim.domain.termsHistory.service.TermsHistoryService;
import shoppingmall.ankim.global.exception.ErrorCode;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(MemberJoinController.class)
@AutoConfigureMockMvc(addFilters = false) // CSRF 비활성화
class MemberJoinControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private TermsHistoryService termsHistoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TermsQueryService termsQueryService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("유효하지 않은 이메일 형식일 때 오류 메시지를 반환한다.")
    void testInvalidEmailFormat() throws Exception {
        // 잘못된 이메일 형식 요청
        MemberEmailRequest request = MemberEmailRequest.builder()
                .id("invalid-email")
                .build();

        mockMvc.perform(post("/api/member/email-check") // post 요청
                        .contentType(MediaType.APPLICATION_JSON) // json 형식 지정
                        .content(objectMapper.writeValueAsString(request))) // json 문자열로 변환(직렬화)
                .andExpect(status().isBadRequest()) // 응답 상태 확인
                .andExpect(jsonPath("$.fieldErrors[0].reason").value("이메일 형식이 올바르지 않습니다.")); // 응답 본문 일치하는지 확인
    }

    @Test
    @DisplayName("중복된 이메일일 때 오류 메시지를 반환한다.")
    void testDuplicateEmail() throws Exception {
        // given : 올바른 이메일 형식의 요청
        MemberEmailRequest request = MemberEmailRequest.builder()
                .id("test@example.com")
                .build();

        doThrow(new MemberRegistrationException(ErrorCode.MEMBER_ID_DUPLICATE)) // 중복 이메일 예외 발생
                .when(memberService).isLoginIdDuplicated(request.getId());

        // when : 이메일 중복 확인 요청
        mockMvc.perform(post("/api/member/email-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // then : 상태 코드와 오류 메시지 확인
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 존재하는 이메일입니다."));  // ApiResponse 내의 메시지 필드 확인
    }

    @Test
    @DisplayName("중복되지 않은 올바른 이메일 형식일 때, 성공 메시지를 반환한다.")
    void testValidAndUniqueEmail() throws Exception {
        // given : 올바른 이메일 형식의 요청
        MemberEmailRequest request = MemberEmailRequest.builder()
                .id("test@example.com")
                .build();

        doNothing().when(memberService).isLoginIdDuplicated(request.getId()); // 예외 발생하지 않도록 설정

        // when: 이메일 중복 확인 요청
        mockMvc.perform(post("/api/member/email-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // then: 상태 코드와 성공 메시지를 확인
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("사용 가능한 이메일입니다."));  // ApiResponse 내의 메시지 필드 확인
    }
}