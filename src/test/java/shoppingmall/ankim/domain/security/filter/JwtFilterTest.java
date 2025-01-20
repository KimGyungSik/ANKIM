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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;
import shoppingmall.ankim.global.exception.ErrorCode;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static shoppingmall.ankim.global.exception.ErrorCode.TOKEN_VALIDATION_ERROR;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private JwtFilter jwtFilter;

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
    @DisplayName("유효한 토큰이 제공되었을 때, 인증 성공 후 SecurityContext에 사용자 정보를 저장한다.")
    void shouldAuthenticateWhenValidToken() throws Exception {
        // given
        String validToken = "validAccessToken";
        given(jwtTokenProvider.isTokenExpired(validToken)).willReturn(false);
        given(jwtTokenProvider.getCategoryFromToken(validToken)).willReturn("access");
        given(jwtTokenProvider.getUsernameFromToken(validToken)).willReturn("testUser");

        request.addHeader("access", validToken);

        // when
        jwtFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("testUser", authentication.getName());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("만료된 토큰이 제공되었을 때, 401 상태코드를 반환한다.")
    void shouldReturnUnauthorizedWhenExpiredToken() throws Exception {
        // given
        String expiredToken = "expiredAccessToken";
        given(jwtTokenProvider.isTokenExpired(expiredToken)).willThrow(new ExpiredJwtException(null, null, "Token expired"));

        request.addHeader("access", expiredToken);

        // when
        jwtFilter.doFilterInternal(request, response, filterChain);

        // then
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("잘못된 카테고리의 토큰이 제공되었을 때, 요청이 차단되고 403 상태코드를 반환한다.")
    void shouldReturnUnauthorizedWhenInvalidTokenCategory() throws Exception {
        // given
        String invalidCategoryToken = "invalidCategoryToken";
        given(jwtTokenProvider.isTokenExpired(invalidCategoryToken)).willReturn(false);
        given(jwtTokenProvider.getCategoryFromToken(invalidCategoryToken)).willReturn("refresh"); // 잘못된 카테고리

        request.addHeader("access", invalidCategoryToken);

        // when
        jwtFilter.doFilterInternal(request, response, filterChain);

        // then
        assertEquals(TOKEN_VALIDATION_ERROR.getHttpStatus().value(), response.getStatus());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("토큰이 제공되지 않았을 때, 필터를 통과하지만 인증 정보는 설정되지 않는다.")
    void shouldSkipFilterWhenNoTokenProvided() throws Exception {
        // given
        jwtFilter.doFilterInternal(request, response, filterChain);

        // when
        verify(filterChain).doFilter(request, response);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
    }
}