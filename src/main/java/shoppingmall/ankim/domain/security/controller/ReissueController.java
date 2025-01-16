package shoppingmall.ankim.domain.security.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import shoppingmall.ankim.domain.security.exception.JwtTokenException;
import shoppingmall.ankim.domain.security.service.ReissueService;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.Map;

import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final ReissueService reissueService;

    @Value("${jwt.refresh.token.expire.time}")
    private long REFRESH_TOKEN_EXPIRE_TIME; // 토큰 만료시간(자동 로그인 X)

    @PostMapping("/reissue")
    public ApiResponse<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("재발행 컨트롤러 ReissueController = ");
        // 헤더에서 Access Token 추출
        String access = request.getHeader("access");

        if (access == null || access.isEmpty()) {
            return ApiResponse.of(ACCESS_TOKEN_NOT_FOUND);
        }

        // 쿠키에서 Refresh Token 추출
        String refreshCookie = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("refresh")) {
                    refreshCookie = cookie.getValue();
                    break;
                }
            }
        }

        // refresh 토큰이 쿠키에 존재하지 않는 경우
        if (refreshCookie == null || refreshCookie.isEmpty()) {
            return ApiResponse.of(REFRESH_TOKEN_NOT_FOUND);
        }

        try {
            // Redis에 access token이 저장되어 있는지 확인
            reissueService.isAccessTokenExist(access);
            System.out.println("isAccessTokenExist = ");

            // Redis에서 access 토큰으로 refresh 토큰 추출 및 검증
            String refreshRedis = reissueService.validateRefreshToken(access);
            // 쿠키에서 꺼낸 refresh와 redis에서 꺼낸 refresh 비교
            if(!refreshCookie.equals(refreshRedis)){
                return ApiResponse.of(INVALID_REFRESH_TOKEN); // 일치하지 않으면 동일한 사용자가 아님
            }

            // 새로운 Access Token 재발급
            Map<String, String> token = reissueService.reissueToken(access, refreshRedis);
            String newAccess = token.get("access");
            String newRefresh = token.get("refresh");

            // 응답 헤더에 Access Token 추가
            response.setHeader("access", newAccess);
//            response.setHeader("Access-Control-Expose-Headers", "access"); // CORS 허용
            // 쿠키에 Refresh Token 추가
            response.addCookie(createCookie("refresh", newRefresh));
            log.info("토큰 재발급 완료");
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
        // cookie.setSecure(true); // https 통신시 사용
        cookie.setPath("/"); // cookie 적용 범위
        cookie.setHttpOnly(true); // javaScript로 접근하지 못하도록 설정
        return cookie;
    }

}
