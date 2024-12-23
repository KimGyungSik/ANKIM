package shoppingmall.ankim.global.handler;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shoppingmall.ankim.domain.security.handler.RedisHandler;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogoutHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisHandler redisHandler;

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // 헤더에서 Access Token 추출
        String access = request.getHeader("access");

        if (access == null || access.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // DB에 토큰이 있는지 확인
        // access token 에서 refresh token 추출
        String refresh = (String) redisHandler.get(access);
        if(refresh == null || refresh.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 토큰 만료여부 확인
        try {
            jwtTokenProvider.isTokenExpired(refresh);
        } catch (ExpiredJwtException e) {
            // 이미 로그아웃이 된 경우
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 토큰이 refresh인지 확인
        String category = jwtTokenProvider.getCategoryFromToken(refresh);
        if(!category.equals("refresh")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 로그아웃 진행
        // refresh를 DB에서 제거
        redisHandler.delete(access);
        // 쿠키에서 access 제거
        Cookie deleteCookie = deleteCookie("access", null);
        response.addCookie(deleteCookie);

        response.setStatus(HttpServletResponse.SC_OK);
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
