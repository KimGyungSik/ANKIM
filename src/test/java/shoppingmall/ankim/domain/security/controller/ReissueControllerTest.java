package shoppingmall.ankim.domain.security.controller;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import shoppingmall.ankim.domain.security.exception.JwtTokenException;
import shoppingmall.ankim.domain.security.handler.RedisHandler;
import shoppingmall.ankim.domain.security.service.ReissueService;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static shoppingmall.ankim.global.exception.ErrorCode.ACCESS_TOKEN_NOT_FOUND;
import static shoppingmall.ankim.global.exception.ErrorCode.REFRESH_TOKEN_EXPIRED;

class ReissueControllerTest {

    @InjectMocks
    private ReissueController reissueController;

    @Mock
    private ReissueService reissueService;

    @Mock
    private RedisHandler redisHandler;

    @BeforeEach
    void setUp() {
        // Mock RedisHandler
        MockitoAnnotations.openMocks(this);
        doReturn(false).when(redisHandler).exists(anyString());
    }

    @Test
    @DisplayName("헤더에 Access 토큰이 없으면 ACCESS_TOKEN_NOT_FOUND를 반환한다.")
    void reissue_noAccessTokenInHeader() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        ApiResponse<?> result = reissueController.reissue(request, response);

        // then
        assertThat(result.getStatus()).isEqualTo(ACCESS_TOKEN_NOT_FOUND.getHttpStatus());
        assertThat(result.getMessage()).isEqualTo(ACCESS_TOKEN_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("Redis에 헤더에 담긴 Access Token이 없으면 ACCESS_TOKEN_NOT_FOUND를 반환한다.")
    void reissue_accessTokenNotInRedis() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("access", "validAccessToken");
        request.setCookies(new Cookie("refresh", "validRefreshToken"));

        doThrow(new JwtTokenException(ACCESS_TOKEN_NOT_FOUND))
                .when(reissueService).isAccessTokenExist("validAccessToken");

        // when
        ApiResponse<?> result = reissueController.reissue(request, response);

        // then
        assertThat(result.getStatus()).isEqualTo(ACCESS_TOKEN_NOT_FOUND.getHttpStatus());
        assertThat(result.getMessage()).isEqualTo(ACCESS_TOKEN_NOT_FOUND.getMessage());

        verify(reissueService, times(1)).isAccessTokenExist("validAccessToken");
        verify(reissueService, never()).validateRefreshToken(anyString());
    }

}