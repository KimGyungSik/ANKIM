package shoppingmall.ankim.domain.login.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shoppingmall.ankim.domain.login.controller.request.LoginRequest;
import shoppingmall.ankim.domain.member.service.MemberService;
import shoppingmall.ankim.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/login")
public class LoginApiController {

//    private final JwtTokenizer jwtTokenizer;
    private final MemberService memberService;
//    private final RefreshTokenService refreshTockenservice;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
//    private final


    public ApiResponse login(@Valid @RequestBody LoginRequest loginRequest) {
//        loginService.login(loginRequest);

        return ApiResponse.ok("");
    }
}
