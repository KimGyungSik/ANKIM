package shoppingmall.ankim.domain.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.web.filter.GenericFilterBean;
import shoppingmall.ankim.domain.security.exception.JwtTokenException;
import shoppingmall.ankim.domain.security.handler.RedisHandler;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;
import shoppingmall.ankim.global.response.ApiResponse;

import java.io.IOException;

import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Slf4j
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisHandler redisHandler;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        // 로그아웃 경로 요청인지 검증
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {
            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equalsIgnoreCase("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 헤더에서 Access Token 추출
        String access = request.getHeader("access");

        if (access == null || access.isEmpty()) {
            log.warn("Access Token이 존재하지 않습니다.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "올바른 요청이 아닙니다.");
            return;
        }

        // DB에 토큰이 있는지 확인
        // access token 에서 refresh token 추출
        String refresh = (String) redisHandler.get(access);
        if(refresh == null || refresh.isEmpty()) {
            log.warn("Refresh Token이 존재하지 않습니다.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "올바른 요청이 아닙니다.");
            return;
        }

        // 토큰 만료여부 확인
        try {
            jwtTokenProvider.isTokenExpired(refresh);
        } catch (ExpiredJwtException e) {
            // 이미 로그아웃이 된 경우
            log.warn("만료된 토큰입니다.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "올바른 요청이 아닙니다.");
            return;
        }

        // 토큰이 refresh인지 확인
        String category = jwtTokenProvider.getCategoryFromToken(refresh);
        if(!category.equals("refresh")) {
            log.warn("잘못된 토큰 카테고리입니다.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "올바른 요청이 아닙니다.");
            return;
        }

        // 로그아웃 진행
        // refresh를 DB에서 제거
        redisHandler.delete(access);
        // 쿠키에서 access 제거
        Cookie deleteCookie = deleteCookie("access", null);
        response.addCookie(deleteCookie);
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"code\":200,\"status\":\"OK\",\"message\": \"로그아웃 되었습니다.\"}");
    }

    private Cookie deleteCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        // 쿠키 설정
        cookie.setHttpOnly(true); // javaScript로 접근하지 못하도록 설정
        cookie.setMaxAge(0); // 쿠키 유효 시간 설정(초단위)
        cookie.setSecure(true); // https 통신시 사용
        cookie.setPath("/"); // cookie 적용 범위
        return cookie;
    }
}
