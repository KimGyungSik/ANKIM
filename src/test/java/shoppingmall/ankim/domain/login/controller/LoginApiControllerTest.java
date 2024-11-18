//package shoppingmall.ankim.domain.login.controller;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.test.web.servlet.MockMvc;
//import shoppingmall.ankim.domain.login.entity.BaseLoginAttempt;
//import shoppingmall.ankim.domain.login.entity.member.loginHistory.MemberLoginAttempt;
//import shoppingmall.ankim.domain.login.repository.member.MemberLoginAttemptRepository;
//import shoppingmall.ankim.domain.login.service.LoginService;
//import shoppingmall.ankim.domain.member.entity.Member;
//import shoppingmall.ankim.domain.member.entity.MemberStatus;
//import shoppingmall.ankim.domain.member.repository.MemberRepository;
//import shoppingmall.ankim.domain.security.service.CustomAuthenticationFailureHandler;
//import shoppingmall.ankim.domain.security.service.JwtTokenProvider;
//import shoppingmall.ankim.global.config.SecurityConfig;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@AutoConfigureMockMvc(addFilters = true) // Security 필터 활성화
//@WebMvcTest(LoginApiController.class)
//@Import({SecurityConfig.class, CustomAuthenticationFailureHandler.class})
//class LoginApiControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private MemberRepository memberRepository;
//
//    @MockBean
//    private MemberLoginAttemptRepository memberLoginAttemptRepository;
//
//    @MockBean
//    private AuthenticationManager authenticationManager;
//
//    @MockBean
//    private JwtTokenProvider jwtTokenProvider;
//
//    @MockBean
//    private LoginService loginService; // MockBean 추가
//
//
//    @Test
//    @DisplayName("비밀번호가 틀린 경우 실패 처리가 수행된다.")
//    void testPasswordFailureHandling() throws Exception {
//        // given
//        String loginId = "failed@example.com";
//        String wrongPassword = "wrong-password";
//
//        Member member = Member.builder()
//                .loginId(loginId)
//                .pwd("password") // 저장된 올바른 비밀번호
//                .name("Test User")
//                .status(MemberStatus.ACTIVE)
//                .build();
//
//        BaseLoginAttempt loginAttemptDetails = BaseLoginAttempt.builder()
//                .failCount(1)
//                .lastAttemptTime(LocalDateTime.now().minusMinutes(1))
//                .unlockTime(null)
//                .activeYn("Y")
//                .build();
//
//        MemberLoginAttempt loginAttempt = MemberLoginAttempt.builder()
//                .member(member)
//                .loginAttemptDetails(loginAttemptDetails)
//                .build();
//
//        // Mocking: MemberRepository 및 MemberLoginAttemptRepository
//        when(memberRepository.findByLoginId(loginId)).thenReturn(member);
//        when(memberLoginAttemptRepository.findByMemberAndLoginAttemptDetailsActiveYn(member, "Y"))
//                .thenReturn(Optional.of(loginAttempt));
//
//        // when
//        mockMvc.perform(post("/api/auth/login")
//                        .param("username", loginId) // 요청 파라미터에 id와 password 전달
//                        .param("password", wrongPassword))
//                .andExpect(status().isUnauthorized()) // 인증 실패 예상
//                .andExpect(content().string("비밀번호가 일치하지 않습니다.")); // 실패 메시지 검증
//
//        // then
//        verify(memberLoginAttemptRepository, times(1)).save(any(MemberLoginAttempt.class));
//        assertEquals(2, loginAttempt.getLoginAttemptDetails().getFailCount());
//    }
//
//}