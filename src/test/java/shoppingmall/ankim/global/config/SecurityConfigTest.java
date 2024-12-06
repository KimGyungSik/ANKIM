package shoppingmall.ankim.global.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(properties = "spring.sql.init.mode=never")
@AutoConfigureMockMvc(addFilters = true)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ThymeleafViewResolver thymeleafViewResolver;

    @Test
    @DisplayName("ADMIN 권한 없이 /admin/** 경로 접근 시 403 에러를 반환한다.")
    @WithAnonymousUser
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/admin"))
//                .andExpect(status().isFound()); // 403 확인
                .andExpect(status().isForbidden()); // 403 확인
    }

    @Test
    @DisplayName("ADMIN 권한으로 /admin 경로 접근 시 200 상태를 반환한다.")
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testAuthorizedAccess() throws Exception {

        when(thymeleafViewResolver.resolveViewName(anyString(), any(Locale.class)))
                .thenReturn(null); // 템플릿 렌더링을 우회( thymleaf가 없어도 테스트 할 수 있음 )

        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk()); // 200 확인
    }

    @Test
    @DisplayName("/ 경로 접근 시 정상적으로 접근 가능하다.")
    @WithAnonymousUser
    void testPermitAllAccess() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk()); // 공개 경로는 접근 가능
    }
}