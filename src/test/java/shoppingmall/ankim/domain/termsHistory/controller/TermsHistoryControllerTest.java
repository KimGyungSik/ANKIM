package shoppingmall.ankim.domain.termsHistory.controller;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import shoppingmall.ankim.domain.termsHistory.service.TermsHistoryService;
import shoppingmall.ankim.global.config.SecurityConfig;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shoppingmall.ankim.global.exception.ErrorCode.EMPTY_TERMS_UPDATE_REQUEST;

@WebMvcTest(TermsHistoryController.class)
@AutoConfigureMockMvc(addFilters = false) // CSRF 비활성화
class TermsHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TermsHistoryService termsHistoryService;

    @Test
    @DisplayName("요청 데이터가 비어 있는 경우 400 에러와 EMPTY_TERMS_UPDATE_REQUEST 코드를 반환한다.")
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void testEmptyRequest() throws Exception {
        // when & then
        mockMvc.perform(post("/api/terms/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]")) // 빈 리스트 전송
                .andExpect(status().isBadRequest()) // 400 상태 코드 확인
                .andExpect(jsonPath("$.status").value("BAD_REQUEST")) // 상태 메시지 확인
                .andExpect(jsonPath("$.code").value(400)) // HTTP 상태 코드 확인
                .andExpect(jsonPath("$.message").value(EMPTY_TERMS_UPDATE_REQUEST.getMessage())); // 에러 메시지 확인
    }

    @Test
    @DisplayName("약관 동의 요청 테스트")
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void testTermsAgreement() throws Exception {
        String requestJson = """
        [
          {
            "terms_no": 4,
            "terms_hist_no": null,
            "terms_hist_agreeYn": "Y"
          },
          {
            "terms_no": 6,
            "terms_hist_no": null,
            "terms_hist_agreeYn": "Y"
          },
          {
            "terms_no": 7,
            "terms_hist_no": null,
            "terms_hist_agreeYn": "Y"
          }
        ]
        """;

        mockMvc.perform(post("/api/terms/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk()) // 요청 성공 확인
                .andExpect(jsonPath("$.status").value("OK")); // 상태 확인
    }

    @Test
    @DisplayName("약관 동의 후 철회 요청 테스트")
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void testTermsAgreementAndRevoke() throws Exception {
        // 첫 번째 요청: 약관 동의
        String initialRequestJson = """
        [
          {
            "terms_no": 4,
            "terms_hist_no": null,
            "terms_hist_agreeYn": "Y"
          },
          {
            "terms_no": 6,
            "terms_hist_no": null,
            "terms_hist_agreeYn": "Y"
          },
          {
            "terms_no": 7,
            "terms_hist_no": null,
            "terms_hist_agreeYn": "Y"
          }
        ]
        """;

        mockMvc.perform(post("/api/terms/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(initialRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));

        // 두 번째 요청: 약관 철회
        String revokeRequestJson = """
        [
          {
            "terms_no": 6,
            "terms_hist_no": null,
            "terms_hist_agreeYn": "N"
          }
        ]
        """;

        mockMvc.perform(post("/api/terms/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(revokeRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK")); // 요청 성공 확인
    }


}