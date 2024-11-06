package shoppingmall.ankim.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import shoppingmall.ankim.domain.member.controller.request.MemberEmailRequest;
import shoppingmall.ankim.domain.member.service.MemberService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false) // CSRF 비활성화
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("유효하지 않은 이메일 형식일 때 오류 메시지 반환")
    void testInvalidEmailFormat() throws Exception {
        // 잘못된 이메일 형식 요청
        MemberEmailRequest request = new MemberEmailRequest();
        request.setId("invalid-email");

        mockMvc.perform(post("/members/email-check") // post 요청
                        .contentType(MediaType.APPLICATION_JSON) // json 형식 지정
                        .content(objectMapper.writeValueAsString(request))) // json 문자열로 변환(직렬화)
                .andExpect(status().isBadRequest()) // 응답 상태 확인
                .andExpect(content().string("이메일 형식이 올바르지 않습니다.")); // 응답 본문 일치하는지 확인
    }

    @Test
    @DisplayName("중복된 이메일일 때 오류 메시지 반환")
    void testDuplicateEmail() throws Exception {
        // 올바른 이메일 형식의 요청
        MemberEmailRequest request = new MemberEmailRequest();
        request.setId("test@ankim.com");

        // 중복된 이메일일 경우 `memberService.emailCheck`가 true를 반환하도록 설정
        when(memberService.emailCheck(request.getId())).thenReturn(true); // 강제로 이메일 중복되었다고 가정

        mockMvc.perform(post("/members/email-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("이미 존재하는 이메일입니다."));
    }

    @Test
    @DisplayName("유효한 이메일 형식이고 중복되지 않은 경우")
    void testValidAndUniqueEmail() throws Exception {
        // 올바른 이메일 형식의 요청
        MemberEmailRequest request = new MemberEmailRequest();
        request.setId("unique@ankim.com");

        // 중복되지 않은 이메일일 경우 `memberService.emailCheck`가 false를 반환하도록 설정
        when(memberService.emailCheck(request.getId())).thenReturn(false); // 강제로 이메일 중복이 없음을 가정

        mockMvc.perform(post("/members/email-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("사용 가능한 이메일입니다."));
    }
}