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
import shoppingmall.ankim.domain.email.service.MailService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                        .param("email", email))
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

        when(mailService.verifyCode(email, code)).thenReturn(true); // 성공 케이스로 설정

        // when, then
        mockMvc.perform(post("/api/mail/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mailRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("OK"));

        // then
        verify(mailService, times(1)).verifyCode(email, code);
    }

    @Test
    @DisplayName("")
    public void test3() throws Exception {
        // given

        // when

        // then

    }

    @Test
    @DisplayName("")
    public void test4() throws Exception {
        // given

        // when

        // then

    }

    @Test
    @DisplayName("")
    public void test5() throws Exception {
        // given

        // when

        // then

    }
}