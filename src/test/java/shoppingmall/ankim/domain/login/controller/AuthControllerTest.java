package shoppingmall.ankim.domain.login.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser // 인증된 사용자로 요청(이거 안하면 401 에러 발생)
    @DisplayName("Refresh Token이 존재하면(로그인) true를 반환한다.")
    void checkLoginStatus_whenRefreshTokenExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/status")
                        .cookie(new Cookie("refresh", "validRefreshToken"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true)); // 로그인 상태 (true)
    }

    @Test
    @WithMockUser
    @DisplayName("Refresh Token이 없으면(비로그인) false를 반환한다.")
    void checkLoginStatus_whenNoRefreshToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/status")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(false)); // 비로그인 상태 (false)
    }
}