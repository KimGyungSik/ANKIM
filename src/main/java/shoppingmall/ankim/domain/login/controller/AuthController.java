package shoppingmall.ankim.domain.login.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shoppingmall.ankim.global.response.ApiResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @GetMapping("/status")
    public ApiResponse<Boolean> checkLoginStatus(@CookieValue(name = "refresh", required = false) String refreshToken) {
        return ApiResponse.ok(refreshToken != null && !refreshToken.isEmpty());
    }
}
