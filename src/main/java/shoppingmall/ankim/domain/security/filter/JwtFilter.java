package shoppingmall.ankim.domain.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.security.dto.CustomUserDetails;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.equals("/api/login/member")
                || path.equals("/api/login/email")
                || path.equals("/docs")
//                || path.equals("/api/mail/send")
                || path.equals("/api/login/admin")
                || path.equals("/reissue");
//                || path.equals("/api/admin/register");
//                || path.equals("/admin/join");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 헤더의 access 키에 담긴 토큰 추출
        String accessToken = request.getHeader("access");
        log.info("Access token: {}", accessToken);

        // 토큰이 없는 경우 다음 필터로
        if(accessToken == null) {
            filterChain.doFilter(request, response);
            // 조건이 해당하면 메서드 종료
            return;
        }

        // 토큰 만료 여부 확인, 만료 시 다음 필터로 넘기지 않음
        try {
            jwtTokenProvider.isTokenExpired(accessToken);
        } catch (ExpiredJwtException e) {
            // response body
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            // response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 토큰이 access인지 확인 (발급 시 페이로드에 명시)
        String category = jwtTokenProvider.getCategoryFromToken(accessToken);
        log.info("Category: {}", category);

        if(!category.equals("access")) {
            // response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            // response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 토큰에서 username(id) 추출
        String username = jwtTokenProvider.getUsernameFromToken(accessToken);
        log.info("Username: {}", username);

        // Member 엔티티 생성
        Member member = Member.builder()
                .loginId(username)
                .pwd("tempPassword")
                .build();

        // UserDetails에 Member 엔티티 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(member);

        // 스프링 시큐리티 인증 토큰을 생성
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        // 세션에 사용자를 등록
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }
}
