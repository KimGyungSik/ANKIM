package shoppingmall.ankim.domain.login.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shoppingmall.ankim.domain.login.controller.request.LoginRequest;
import shoppingmall.ankim.domain.login.exception.AdminLoginFailedException;
import shoppingmall.ankim.domain.login.exception.MemberLoginFailedException;
import shoppingmall.ankim.domain.login.service.LoginService;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.Map;

import static shoppingmall.ankim.global.exception.ErrorCode.INVALID_CREDENTIALS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/login")
public class LoginApiController {

    private final LoginService loginService;

    @Value("${jwt.refresh.token.expire.time}")
    private long REFRESH_TOKEN_EXPIRE_TIME;

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider; // JWT 토큰 생성기

    @PostMapping("/member")
    public ApiResponse<?> memberLogin(@RequestBody @Valid LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) throws MemberLoginFailedException {
        try {
            // LoginService를 통해 인증 처리
            Map<String, Object> jwtToken = loginService.memberLogin(loginRequest.toServiceRequest(), request);
            String access = (String) jwtToken.get("access");
            String refresh = (String) jwtToken.get("refresh");
            long expireTime = (long) jwtToken.get("expireTime");

            // 성공 시 토큰 반환
            // 응답 설정
            response.setHeader("access", access); // access토큰 헤더에 저장
            response.addCookie(createCookie("refresh", refresh, expireTime)); // refresh토큰 쿠키에 저장
            response.setStatus(HttpStatus.OK.value());

            return ApiResponse.ok("로그인 성공");

        } catch (BadCredentialsException ex) {
            throw new MemberLoginFailedException(INVALID_CREDENTIALS);
        }
    }

    @PostMapping("/admin") // FIXME 관리자 로그인 로직
    public ApiResponse<?> adminLogin(@RequestBody @Valid LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) throws MemberLoginFailedException {
        try {
            // LoginService를 통해 인증 처리
            Map<String, Object> jwtToken = loginService.adminLogin(loginRequest.toServiceRequest(), request);
            String access = (String) jwtToken.get("access");
            String refresh = (String) jwtToken.get("refresh");
            long expireTime = (long) jwtToken.get("expireTime");

            // 성공 시 토큰 반환
            // 응답 설정
            response.setHeader("access", access); // access토큰 헤더에 저장
            response.addCookie(createCookie("refresh", refresh, expireTime)); // refresh토큰 쿠키에 저장
            response.setStatus(HttpStatus.OK.value());

            return ApiResponse.ok("로그인 성공");

        } catch (BadCredentialsException ex) {
            throw new AdminLoginFailedException(INVALID_CREDENTIALS);
        }
    }

    private Cookie createCookie(String key, String value, long expireTime) {
        Cookie cookie = new Cookie(key, value);
        // 쿠키 설정
        cookie.setHttpOnly(true); // javaScript로 접근하지 못하도록 설정
        cookie.setMaxAge((int) expireTime / 1000); // 쿠키 유효 시간 설정(초단위)
//        cookie.setSecure(true); // https 통신시 사용
//        cookie.setPath("/"); // cookie 적용 범위
        return cookie;
    }

}
