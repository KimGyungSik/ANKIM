package shoppingmall.ankim.domain.email.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import shoppingmall.ankim.domain.email.controller.request.MailRequest;
import shoppingmall.ankim.domain.email.exception.MailSendException;
import shoppingmall.ankim.domain.email.service.Count;
import shoppingmall.ankim.domain.email.service.MailService;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shoppingmall.ankim.global.exception.ErrorCode.MAIL_SEND_FAIL;

@WebMvcTest(MailController.class)
@AutoConfigureMockMvc(addFilters = false)
class MailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MailService mailService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("사용자가 인증 메일 요청하는 경우 메일이 전송되는지 확인한다.")
    public void sendMailTest() throws Exception {
        // given
        String email = "test@example.com";
        String code = "123456";
        when(mailService.generateCode()).thenReturn(code);
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailService.createMail(email, code)).thenReturn(mimeMessage);

        // when, then
        mockMvc.perform(post("/api/mail/send")
                        .param("id", email))
                .andExpect(status().isOk());

        // then
        verify(mailService, times(1)).sendMail(mimeMessage); // 메일이 한 번 전송되었는지 확인
    }

    @Test
    @DisplayName("사용자가 알맞은 인증번호를 입력하고 검증 요청을 보내는 경우 성공하는지 확인한다.")
    public void test2() throws Exception {
        // given
        String email = "test@example.com";
        String code = "123456";
        when(mailService.generateCode()).thenReturn(code);
        MailRequest mailRequest = new MailRequest(email, code);

        when(mailService.verifyCode(email, code)).thenReturn(Count.SUCCESS); // 성공 케이스로 설정

        // when, then
        mockMvc.perform(post("/api/mail/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mailRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("SUCCESS"));

        // then
        verify(mailService, times(1)).verifyCode(email, code);
    }

    @Test
    @DisplayName("잘못된 이메일 형식으로 인증 메일 요청 시 오류 메시지를 반환한다.")
    public void sendMailWithInvalidEmailFormat() throws Exception {
        // given
        String invalidEmail = "testexample.com";

        // when, then
        mockMvc.perform(post("/api/mail/send")
                        .param("email", invalidEmail))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("메일 전송 실패 시 서버 오류 메시지를 반환한다.")
    public void sendMailFailure() throws Exception {
        // given
        String email = "test@example.com";
        String code = "123456";
        when(mailService.generateCode()).thenReturn(code);
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailService.createMail(email, code)).thenReturn(mimeMessage);
        doThrow(new MailSendException(MAIL_SEND_FAIL)).when(mailService).sendMail(mimeMessage);

        // when, then
        mockMvc.perform(post("/api/mail/send")
                        .param("id", email))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("서버에서 문제가 발생했습니다. 잠시 후 다시 시도해주세요."));
    }

    @Test
    @DisplayName("인증번호 재발급 시 기존 인증번호가 갱신되는지 확인한다.")
    public void resendCodeUpdatesCode() throws Exception {
        // given
        String email = "test@example.com";
        String firstCode = "123456";
        String secondCode = "654321";
        assertNotEquals(firstCode, secondCode);
        MimeMessage mimeMessage = mock(MimeMessage.class);

        // 첫 번째 sendMail 호출 시 첫 번째 코드 반환
        when(mailService.generateCode()).thenReturn(firstCode);
        when(mailService.createMail(email, firstCode)).thenReturn(mimeMessage);

        // when : 첫 번째로 인증번호 전송
        mockMvc.perform(post("/api/mail/send")
                        .param("id", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("메일 전송 완료"));

        // given : 두 번째 sendMail 호출 시 두 번째 코드 반환
        when(mailService.generateCode()).thenReturn(secondCode);
        when(mailService.createMail(email, secondCode)).thenReturn(mimeMessage);

        // when : 두 번째로 인증번호 전송 (재발급)
        mockMvc.perform(post("/api/mail/send")
                        .param("id", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("메일 전송 완료"));

        // then : 메일 전송이 두 번 이루어졌는지, 각 코드가 제대로 설정되었는지 확인
        verify(mailService, times(2)).generateCode();
        verify(mailService, times(2)).sendMail(mimeMessage);
    }

    @Test
    @DisplayName("잘못된 인증번호 3회 시도 후, 새 인증번호 요청으로 인증 성공하는지 확인한다.")
    public void resendAfterThreeFailuresThenSuccess() throws Exception {
        // given: 이메일과 초기 인증번호 설정
        String email = "test@example.com";
        String initialCode = "123456";
        String newCode = "654321";

        // Mock 객체 설정
        MimeMessage mimeMessage = mock(MimeMessage.class);

        // given: 초기 인증번호 생성 및 이메일 전송 설정
        when(mailService.generateCode()).thenReturn(initialCode);
        when(mailService.createMail(email, initialCode)).thenReturn(mimeMessage);

        // when: 이메일 전송 및 초기 인증번호 저장
        mockMvc.perform(post("/api/mail/send")
                        .param("id", email))
                .andExpect(status().isOk());

        // given: 인증번호 3회 틀리기 설정
        when(mailService.verifyCode(email, "wrongCode")).thenReturn(Count.FAIL);

        // when: 인증번호 3회 틀리기
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/api/mail/verify")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new MailRequest(email, "wrongCode"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("FAIL"));
        }

        // given: 3회 실패 후 "RETRY" 설정
        when(mailService.verifyCode(email, "wrongCode")).thenReturn(Count.RETRY);

        // when: 3회 실패 후 "RETRY" 반환 확인
        mockMvc.perform(post("/api/mail/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MailRequest(email, "wrongCode"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("RETRY"));

        // given: 새로운 인증번호 생성 및 전송 설정
        when(mailService.generateCode()).thenReturn(newCode);
        when(mailService.createMail(email, newCode)).thenReturn(mimeMessage);

        // when: 새로운 인증번호 전송
        mockMvc.perform(post("/api/mail/send")
                        .param("id", email))
                .andExpect(status().isOk());

        // given: 새로운 인증번호로 성공적인 검증 설정
        when(mailService.verifyCode(email, newCode)).thenReturn(Count.SUCCESS);

        // when: 새로운 인증번호로 성공적인 검증 요청
        mockMvc.perform(post("/api/mail/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new MailRequest(email, newCode))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("SUCCESS"));
    }

}