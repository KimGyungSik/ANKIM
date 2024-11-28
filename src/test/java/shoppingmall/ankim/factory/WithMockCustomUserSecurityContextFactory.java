package shoppingmall.ankim.factory;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.member.entity.MemberStatus;
import shoppingmall.ankim.domain.security.dto.CustomUserDetails;
import shoppingmall.ankim.domain.security.service.JwtTokenProvider;
import shoppingmall.ankim.security.WithMockCustomUser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    private final JwtTokenProvider jwtTokenProvider;

    public WithMockCustomUserSecurityContextFactory(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // 권한 생성
        var authorities = Arrays.stream(annotation.roles())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // 사용자 정보 생성 (CustomUserDetails 사용)
        Member member = Member.builder()
                .loginId(annotation.username())
                .pwd(annotation.password())
                .name("홍길동")
                .phoneNum("010-1234-5678")
                .birth(LocalDate.of(1990, 1, 1))
                .grade(50)
                .gender("M")
                .joinDate(LocalDateTime.now())
                .status(MemberStatus.ACTIVE)
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails(member);

        // JWT 생성
        String jwt = jwtTokenProvider.generateAccessToken(customUserDetails, "access");

        // SecurityContext에 Authentication 설정
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(customUserDetails, jwt, authorities);

        context.setAuthentication(authentication);
        return context;
    }
}