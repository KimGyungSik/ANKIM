package shoppingmall.ankim.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import shoppingmall.ankim.domain.security.service.CustomAuthenticationFailureHandler;
import shoppingmall.ankim.domain.security.service.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity // 모든 요청 URL이 스프링 시큐리티 제어 받게 만듦
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 비밀번호 암호화
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // csrf 비활성화
        http
                .csrf((auth) -> auth.disable());

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
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN") // 관리자 접근 경로에 관리자 권한 필요
                        .requestMatchers("/my/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN") // 마이페이지는 USER, ADMIN 모두 접근 가능
                        .anyRequest().permitAll()) // 나머지 요청은 접근 허용
        ;

/*        http
                .formLogin(login -> login
//                        .loginPage("login")
                                .loginProcessingUrl("/api/login/member")
                                .failureHandler(customAuthenticationFailureHandler) // 실패 핸들러 등록
                                .permitAll()
                );*/

        // 세션 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // JWT에서는 세션을 stateless 상태로 사용


        // JWT 필터 추가
        http
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}
