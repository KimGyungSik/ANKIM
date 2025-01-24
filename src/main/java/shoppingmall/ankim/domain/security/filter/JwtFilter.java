package shoppingmall.ankim.domain.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.filter.OncePerRequestFilter;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.security.dto.CustomUserDetails;
import shoppingmall.ankim.domain.security.exception.JwtTokenException;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;
import shoppingmall.ankim.global.exception.ErrorCode;
import shoppingmall.ankim.global.response.ApiResponse;

import java.io.IOException;
import java.io.PrintWriter;

import static shoppingmall.ankim.global.exception.ErrorCode.EXPIRED_JWT_TOKEN;
import static shoppingmall.ankim.global.exception.ErrorCode.TOKEN_VALIDATION_ERROR;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환기

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

        // 토큰 형식 확인, 형식이 맞지 않는 경우 다음 필터로 넘기지 않음
        try {
            jwtTokenProvider.isTokenValidate(accessToken);
        } catch (JwtTokenException e) {
            sendErrorResponse(response, e.getErrorCode());
            return;
        }

        // 토큰 만료 여부 확인, 만료 시 다음 필터로 넘기지 않음
        try {
            jwtTokenProvider.isTokenExpired(accessToken);
        } catch (ExpiredJwtException e) {
            sendErrorResponse(response, EXPIRED_JWT_TOKEN);
            return;
        }

        // 토큰이 access인지 확인 (발급 시 페이로드에 명시)
        String category = jwtTokenProvider.getCategoryFromToken(accessToken);
        log.info("Category: {}", category);

        if(!category.equals("access")) {
            sendErrorResponse(response, TOKEN_VALIDATION_ERROR);
            return;
        }

        // 토큰에서 username(id) 추출
        String username = jwtTokenProvider.getUsernameFromToken(accessToken);
        log.info("Username: {}", username);

        // Member 엔티티 생성
        Member member = Member.builder()
                .loginId(username)
                .password("tempPassword")
                .build();

        // UserDetails에 Member 엔티티 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(member);

        // 스프링 시큐리티 인증 토큰을 생성
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        // 세션에 사용자를 등록
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }

    // 필터에서 에러메시지 반환하기 위한 메서드
    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(errorCode.getHttpStatus().value());

        // JSON 형식으로 응답 작성
        PrintWriter writer = response.getWriter();
        ApiResponse<Void> errorResponse = ApiResponse.of(errorCode);

        writer.print(objectMapper.writeValueAsString(errorResponse));
        writer.flush();
    }
}
