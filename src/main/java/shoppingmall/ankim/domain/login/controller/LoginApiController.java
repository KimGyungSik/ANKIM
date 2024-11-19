package shoppingmall.ankim.domain.login.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shoppingmall.ankim.domain.login.controller.request.LoginRequest;
import shoppingmall.ankim.domain.login.exception.LoginFailedException;
import shoppingmall.ankim.domain.login.service.LoginService;
import shoppingmall.ankim.domain.login.service.request.LoginServiceRequest;
import shoppingmall.ankim.domain.member.dto.MemberResponse;
import shoppingmall.ankim.domain.member.service.MemberService;
import shoppingmall.ankim.domain.security.dto.CustomUserDetails;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;
import shoppingmall.ankim.global.exception.ErrorCode;
import shoppingmall.ankim.global.response.ApiResponse;

import javax.security.auth.login.LoginException;

import java.util.Map;

import static shoppingmall.ankim.global.exception.ErrorCode.INVALID_CREDENTIALS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class LoginApiController {

    private final LoginService loginService;

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider; // JWT 토큰 생성기

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody @Valid LoginRequest loginRequest, HttpServletResponse response) throws LoginFailedException {
        try {
            // LoginService를 통해 인증 처리
            String jwtToken = loginService.login(loginRequest.toServiceRequest());

            // 성공 시 토큰 반환
            // 헤더에 토큰 추가
            response.addHeader("Authorization", "Bearer " + jwtToken);

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
}
