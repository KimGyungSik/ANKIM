package shoppingmall.ankim.domain.security.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.security.dto.CustomUserDetails;

import java.io.IOException;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // request에서 Authorization 헤더 조회
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null && !authHeader.startsWith("Bearer ")) {
            log.info("token null");
            filterChain.doFilter(request, response); // 다음 필터로 request, response를 넘김

            // 조건이 해당하면 메서드 종료 ( 필수 )
            return;
        }

        String token = authHeader.split(" ")[1]; // "Bearer " 제거

        // 토큰 소멸시간 검증
        if (jwtTokenProvider.isTokenExpired(token)) {

            log.info("token expired");
            filterChain.doFilter(request, response);

            // 조건에 해당하면 메서드 종료 ( 필수 )
            return;
        }

        // 토큰에서 username(id)와 role 추출
        String username = jwtTokenProvider.getUsernameFromToken(token);
        System.out.println("token에서 Id 추출 = " + username);
//        jwtTokenProvider.get

        // Member 엔티티 생성
        Member member = Member.builder()
                .loginId(username)
                .pwd("tempPassword")
                .build();

        // UserDetails에 Member 엔티티 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(member);
        System.out.println(customUserDetails.getAuthorities());

        // 스프링 시큐리티 인증 토큰을 생성
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        // 세션에 사용자를 등록
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);

    }
}
