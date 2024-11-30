package shoppingmall.ankim.domain.member.controller;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.service.MemberMyPageService;
import shoppingmall.ankim.domain.member.service.MemberService;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;
import shoppingmall.ankim.factory.MemberJwtFactory;

import javax.swing.text.html.parser.Entity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Security 비활성화
@Transactional
class MemberMyPageApiControllerTest {

    @Autowired
    EntityManager em;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberMyPageService memberMyPageService;

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("새로운 비밀번호 형식이 틀린 경우 400 Bad Request를 반환한다.")
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void changePassword_Fail_InvalidNewPasswordFormat() throws Exception {
        // given
        String loginId = "test@ankim.com";
        Member member = MemberJwtFactory.createMember(em, loginId);
        String accessToken = MemberJwtFactory.createAccessToken(member, jwtTokenProvider);

        // 쿠키 생성
        Cookie jwtCookie = new Cookie("access", accessToken);

        String requestBody = """
                {
                    "oldPassword": "password123",
                    "newPassword": "short",
                    "confirmPassword": "short"
                }
                """;

        // when & then
        mockMvc.perform(put("/api/mypage/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .cookie(jwtCookie)) // 쿠키 추가
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("newPassword"))
                .andExpect(jsonPath("$.fieldErrors[0].reason").value("새로운 비밀번호는 8~20자 이내의 영문 대소문자, 숫자, 특수문자를 포함해야 합니다."));
    }
}