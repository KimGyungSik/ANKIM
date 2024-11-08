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
import shoppingmall.ankim.domain.member.service.MemberService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(MemberJoinController.class)
@AutoConfigureMockMvc(addFilters = false) // CSRF 비활성화
class MemberJoinControllerTest {

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

        mockMvc.perform(post("/api/member/email-check") // post 요청
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

        // 중복된 이메일일 경우 memberService.emailCheck가 true를 반환하도록 설정
        when(memberService.emailCheck(request.getId())).thenReturn(true); // 강제로 이메일 중복되었다고 가정

        mockMvc.perform(post("/api/member/email-check")
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

        // 중복되지 않은 이메일일 경우 memberService.emailCheck가 false를 반환하도록 설정
        when(memberService.emailCheck(request.getId())).thenReturn(false); // 강제로 이메일 중복이 없음을 가정

        mockMvc.perform(post("/api/member/email-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("사용 가능한 이메일입니다."));
    }

    @Test
    @DisplayName("이메일을 입력한 뒤 다음 단계로 넘어갈 때 id값이 잘전달되는지 확인한다.")
    public void test1() throws Exception {
        // given
        String validId = "test@example.com";

        // when & then
        mockMvc.perform(post("/api/member/email-next")
                        .param("id", validId))
                .andExpect(status().isOk())
                .andExpect(content().string("registerNext"));
    }

    @Test
    @DisplayName("이메일을 입력한 뒤 다음 단계로 넘어갈 때 id값이 전달되지 않은 경우 지정한 에러가 발생하는지 확인한다.")
    public void test2() throws Exception {
        // given
        String validId = null;

        // when & then
        mockMvc.perform(post("/api/member/email-next")
                .param("id", validId))
                .andExpect(status().is5xxServerError())
                .andExpect(content().string("서버에서 문제가 발생했습니다. 잠시 후 다시 시도해주세요."));

    }
}