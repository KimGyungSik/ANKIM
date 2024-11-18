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
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import shoppingmall.ankim.domain.member.controller.request.MemberEmailRequest;
import shoppingmall.ankim.domain.member.exception.MemberRegistrationException;
import shoppingmall.ankim.domain.member.service.MemberService;
import shoppingmall.ankim.domain.security.service.CustomUserDetailsService;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;
import shoppingmall.ankim.domain.terms.service.query.TermsQueryService;
import shoppingmall.ankim.domain.termsHistory.service.TermsHistoryService;
import shoppingmall.ankim.global.exception.ErrorCode;

import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false) // CSRF 비활성화
class MemberControllerTest {

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
    private ThymeleafViewResolver thymeleafViewResolver;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;


    @Test
    @DisplayName("이메일을 입력한 뒤 다음 단계로 넘어갈 때 id값이 잘전달되는지 확인한다.")
    public void idParameterCheck() throws Exception {
        // given
        String validId = "test@example.com";

        // when & then
        when(thymeleafViewResolver.resolveViewName(anyString(), any(Locale.class)))
                .thenReturn(null); // 템플릿 렌더링을 우회( thymleaf가 없어도 테스트 할 수 있음 )

        mockMvc.perform(get("/member/email-next")
                        .param("loginId", validId))
                .andExpect(status().isOk()) // 상태 코드만 확인
        ;
    }

    @Test
    @DisplayName("이메일을 입력한 뒤 다음 단계로 넘어갈 때 id값이 전달되지 않은 경우 CLIENT_ERROR 에러가 발생하는지 확인한다.")
    public void testMissingIdParameter() throws Exception {
        // given
        String validId = null;

        // when & then
        mockMvc.perform(get("/member/email-next")
                .param("loginId", validId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("이메일을 입력한 뒤 다음 단계로 넘어갈 때 id값이 전달되지 않은 경우 INTERNAL_SERVER_ERROR 에러가 발생하는지 확인한다.")
    public void testMissingIdParameterWithCustomException() throws Exception {
        // given
        String validId = "";

        // when & then
        mockMvc.perform(get("/member/email-next")
                        .param("loginId", validId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("서버에서 문제가 발생했습니다. 잠시 후 다시 시도해주세요."));
    }
}