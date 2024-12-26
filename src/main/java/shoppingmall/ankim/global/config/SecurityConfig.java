package shoppingmall.ankim.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import shoppingmall.ankim.domain.security.filter.CustomLogoutFilter;
import shoppingmall.ankim.domain.security.filter.JwtFilter;
import shoppingmall.ankim.domain.security.handler.RedisHandler;
import shoppingmall.ankim.domain.security.service.*;

@Configuration
@EnableWebSecurity // 모든 요청 URL이 스프링 시큐리티 제어 받게 만듦
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisHandler redisHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // 비밀번호 암호화
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, JwtTokenProvider jwtTokenProvider) throws Exception {

        // csrf 비활성화
        http
                .csrf((auth) -> auth.disable());

        // 세션 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // JWT에서는 세션을 stateless 상태로 사용

//         Form 로그인 방식 비활성화
//        http
//                .formLogin((auth) -> auth.disable());

        // http basic 인증 방식 비활성화
        http
                .httpBasic((auth) -> auth.disable());

        // 경로별 인가 설정
        http
                .authorizeHttpRequests((authorize) -> authorize
//                        .requestMatchers("/my/**").authenticated() // my라는 url로 들어오면 인증이 필요
                        .requestMatchers("/admin/join", "/api/admin/register").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN") // 관리자 접근 경로에 관리자 권한 필요
                        .requestMatchers("/mypage/**").hasAnyRole("USER", "ADMIN") // 마이페이지는 USER, ADMIN 모두 접근 가능
                        .anyRequest().permitAll()) // 나머지 요청은 접근 허용
        ;

        // JWT 필터 추가
        http
                .addFilterAfter(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        // logout 필터 추가
        http
                .addFilterBefore(new CustomLogoutFilter(jwtTokenProvider, redisHandler), LogoutFilter.class);

        return http.build();
    }

}
