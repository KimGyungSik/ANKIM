package shoppingmall.ankim.domain.security.service;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.security.dto.CustomUserDetails;
import shoppingmall.ankim.domain.security.exception.JwtTokenException;

import java.util.HashMap;
import java.util.Map;

import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReissueServiceImpl implements ReissueService {

    private final JwtTokenProvider jwtTokenProvider;

    // Refresh Token 검증
    public void validateRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new JwtTokenException(REFRESH_TOKEN_NOT_FOUND);
        }

        // 만료 여부 확인
        try {
            jwtTokenProvider.isTokenExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new JwtTokenException(REFRESH_TOKEN_EXPIRED);
        }

        // 토큰 타입 확인
        String category = jwtTokenProvider.getCategoryFromToken(refreshToken);
        if (!"refresh".equals(category)) {
            throw new JwtTokenException(INVALID_REFRESH_TOKEN);
        }
    }

    // Access Token 재발급
    public Map<String, String> reissueToken(String refreshToken) {
        // Refresh Token에서 사용자 정보 추출
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

        // Member 엔티티 생성
        Member member = Member.builder()
                .loginId(username)
                .pwd("tempPassword")
                .build();

        // UserDetails에 Member 엔티티 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(member);

        // 새로운 Token 생성
        String access = jwtTokenProvider.generateAccessToken(customUserDetails,"access");
        String refresh = jwtTokenProvider.generateRefreshToken(customUserDetails,"refresh");

        Map<String, String> map = new HashMap<>();
        map.put("access", access);
        map.put("refresh", refresh);

        return map;
    }
}
