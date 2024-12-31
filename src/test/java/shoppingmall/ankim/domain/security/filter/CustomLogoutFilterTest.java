package shoppingmall.ankim.domain.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import shoppingmall.ankim.domain.security.handler.RedisHandler;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomLogoutFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RedisHandler redisHandler;

    @InjectMocks
    private CustomLogoutFilter customLogoutFilter;

    @Mock
    private FilterChain filterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("유효한 Access Token과 Refresh Token이 제공되었을 때 성공적으로 로그아웃된다.")
    void shouldLogoutSuccessfullyWhenValidTokens() throws Exception {
        // given
        String validAccessToken = "validAccessToken";
        String validRefreshToken = "validRefreshToken";
        given(redisHandler.get(validAccessToken)).willReturn(validRefreshToken);
        given(jwtTokenProvider.isTokenExpired(validRefreshToken)).willReturn(false);
        given(jwtTokenProvider.getCategoryFromToken(validRefreshToken)).willReturn("refresh");

        request.setRequestURI("/logout");
        request.setMethod("POST");
        request.addHeader("access", validAccessToken);

        // when
        customLogoutFilter.doFilter(request, response, filterChain);

        // then
        verify(redisHandler).delete(validAccessToken);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    @DisplayName("Access Token이 제공되지 않았을 때 400 상태코드를 반환한다.")
    void shouldReturnBadRequestWhenNoAccessToken() throws Exception {
        // given
        request.setRequestURI("/logout");
        request.setMethod("POST");

        // when
        customLogoutFilter.doFilter(request, response, filterChain);

        // then
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
        verify(redisHandler, never()).delete(anyString());
    }

    @Test
    @DisplayName("만료된 Refresh Token이 제공되었을 때 400 상태코드를 반환한다.")
    void shouldReturnBadRequestWhenRefreshTokenExpired() throws Exception {
        // given
        String validAccessToken = "validAccessToken";
        String expiredRefreshToken = "expiredRefreshToken";
        given(redisHandler.get(validAccessToken)).willReturn(expiredRefreshToken);
        given(jwtTokenProvider.isTokenExpired(expiredRefreshToken)).willThrow(new ExpiredJwtException(null, null, "Token expired"));

        request.setRequestURI("/logout");
        request.setMethod("POST");
        request.addHeader("access", validAccessToken);

        // when
        customLogoutFilter.doFilter(request, response, filterChain);

        // then
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
        verify(redisHandler, never()).delete(validAccessToken);
    }

    @Test
    @DisplayName("로그아웃 경로가 아닌 요청일 경우 필터 체인이 정상적으로 이어진다.")
    void shouldProceedToNextFilterWhenNotLogoutPath() throws Exception {
        // given
        request.setRequestURI("/not-logout-path");
        request.setMethod("POST");

        // when
        customLogoutFilter.doFilter(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response); // 필터 체인이 이어져야 함
        assertEquals(HttpServletResponse.SC_OK, response.getStatus()); // 응답 상태는 변경되지 않아야 함
    }
}