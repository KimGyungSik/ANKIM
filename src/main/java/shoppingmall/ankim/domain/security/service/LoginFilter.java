package shoppingmall.ankim.domain.security.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import shoppingmall.ankim.domain.security.dto.CustomUserDetails;


@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws AuthenticationException {

        String username = obtainUsername(req);
        String password = obtainPassword(req);

        System.out.println("username = " + username);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        return this.authenticationManager.authenticate(authToken);
    }

    // 로그인 성공시 실행(jwt 발급)
    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) {
        log.info("successful authentication");

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        String token = jwtTokenProvider.generateAccessToken(userDetails, "");

        res.addHeader("Authorization", "Bearer " + token);
    }

    // 로그인 실패시 실행
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest req, HttpServletResponse res, AuthenticationException failed) {
        log.info("unsuccessful authentication");
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
