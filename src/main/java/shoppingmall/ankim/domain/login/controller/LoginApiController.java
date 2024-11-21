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
import shoppingmall.ankim.domain.login.exception.LoginFailedException;
import shoppingmall.ankim.domain.login.service.LoginService;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;
import shoppingmall.ankim.global.response.ApiResponse;

import java.util.Map;

import static shoppingmall.ankim.global.exception.ErrorCode.INVALID_CREDENTIALS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class LoginApiController {

    private final LoginService loginService;

    @Value("${jwt.refresh.token.expire.time}")
    private long REFRESH_TOKEN_EXPIRE_TIME;

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider; // JWT 토큰 생성기

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody @Valid LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) throws LoginFailedException {
        try {
            // LoginService를 통해 인증 처리
            Map<String, String> jwtToken = loginService.login(loginRequest.toServiceRequest(), request);
            String access = jwtToken.get("access");
            String refresh = jwtToken.get("refresh");

            // 성공 시 토큰 반환
            // 응답 설정
            response.setHeader("access", access);
            response.addCookie(createCookie("refresh", refresh));
            response.setStatus(HttpStatus.OK.value());

            // 헤더에 토큰 추가
//            response.addHeader("Authorization", "Bearer " + jwtToken);

            return ApiResponse.ok("로그인 성공");

/*            // Spring Security 인증 시도
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getLoginId(), loginRequest.getPwd())
            );

            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

            // 인증 성공 시 private boolean autoLogin 설정을 했는지 확인
            if (loginRequest.isAutoLogin()) {
                // Access Token 및 Refresh Token 생성
                String accessToken = jwtTokenProvider.generateAccessToken(customUserDetails);
                String refreshToken = jwtTokenProvider.generateRefreshToken(customUserDetails);


                Map<String, String> tokens = Map.of(
                        "accessToken", accessToken,
                        "refreshToken", refreshToken
                );

                return ApiResponse.ok(tokens);
            }*/
        } catch (BadCredentialsException ex) {
            throw new LoginFailedException(INVALID_CREDENTIALS);
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
