package shoppingmall.ankim.domain.security.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import shoppingmall.ankim.domain.security.exception.JwtTokenException;
import shoppingmall.ankim.domain.security.service.ReissueService;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.Map;

import static shoppingmall.ankim.global.exception.ErrorCode.COOKIE_NOT_INCLUDED;
import static shoppingmall.ankim.global.exception.ErrorCode.REFRESH_TOKEN_NOT_FOUND;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final ReissueService reissueService;

    @Value("${jwt.refresh.token.expire.time}")
    private long REFRESH_TOKEN_EXPIRE_TIME;

    @PostMapping("/reissue")
    public ApiResponse<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        // cookie에서 Refresh Token 추출
        String refresh = null;
        Cookie[] cookies = request.getCookies();

        // 쿠키 배열이 null인 경우 처리 (쿠키 자체가 없음)
        if (cookies == null || cookies.length == 0) {
            return ApiResponse.of(COOKIE_NOT_INCLUDED);
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {
            return ApiResponse.of(REFRESH_TOKEN_NOT_FOUND);
        }

        try {
            // Refresh Token 검증
            reissueService.validateRefreshToken(refresh);

            // 새로운 Access Token 재발급
            Map<String, String> token = reissueService.reissueToken(refresh);
            String newAccess = token.get("access");
            String newRefresh = token.get("refresh");

            // 응답 헤더에 Access Token 추가
            response.setHeader("access", newAccess);
            response.addCookie(createCookie("refresh", newRefresh));

            return ApiResponse.ok("토큰 재발급 완료");
        } catch (JwtTokenException e) {
            return ApiResponse.of(e.getErrorCode());
        } catch (Exception e) {
            return ApiResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);

        // 쿠키 설정
        cookie.setMaxAge((int) REFRESH_TOKEN_EXPIRE_TIME / 1000); // 쿠키 유효 시간 설정
/*
        cookie.setSecure(true); // https 통신시 사용
        cookie.setPath("/"); // cookie 적용 범위
*/
        cookie.setHttpOnly(true); // javaScript로 접근하지 못하도록 설정

        return cookie;
    }

}
