package shoppingmall.ankim.domain.security.service;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import shoppingmall.ankim.domain.member.entity.Member;
import shoppingmall.ankim.domain.security.dto.CustomUserDetails;
import shoppingmall.ankim.domain.security.exception.JwtTokenException;
import shoppingmall.ankim.domain.security.handler.RedisHandler;

import java.util.HashMap;
import java.util.Map;

import static shoppingmall.ankim.global.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReissueServiceImpl implements ReissueService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisHandler redisHandler;

    @Value("${jwt.refresh.token.expire.time}")
    private long REFRESH_TOKEN_EXPIRE_TIME;

    @Override
    public String validateRefreshToken(String access) {
        String refresh = (String) redisHandler.get(access);
        if(refresh == null || refresh.isEmpty()) {
            throw new JwtTokenException(ACCESS_TOKEN_NOT_FOUND); // access 토큰이 존재하지 않기 때문에 발생
        }

        // 만료 여부 확인
        try {
            jwtTokenProvider.isTokenExpired(refresh);
        } catch (ExpiredJwtException e) {
            // FIXME Refresh 토큰이 만료된 경우 로그아웃 또는 재인증 요청
            redisHandler.delete(access); // Refresh Token이 만료되었으므로 Redis에서 해당 데이터를 삭제
            throw new JwtTokenException(REFRESH_TOKEN_EXPIRED);
        }

        // 토큰 타입 확인
        String category = jwtTokenProvider.getCategoryFromToken(refresh);
        if (!"refresh".equals(category)) {
            throw new JwtTokenException(INVALID_REFRESH_TOKEN);
        }
        return refresh;
    }

    // Access Token 재발급
    public Map<String, String> reissueToken(String accessToken, String refreshToken) {
        try {
            // Refresh Token에서 사용자 정보 추출
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

            // Member 엔티티 생성
            Member member = Member.builder()
                    .loginId(username)
                    .pwd("tempPassword") // 가짜 비밀번호
                    .build();

            // UserDetails에 Member 엔티티 담기
            CustomUserDetails customUserDetails = new CustomUserDetails(member);

            // 새로운 Token 생성
            String newAccess = jwtTokenProvider.generateAccessToken(customUserDetails,"access");
            String newRefresh = jwtTokenProvider.generateRefreshToken(customUserDetails,"refresh");

            Map<String, String> map = new HashMap<>();
            map.put("access", newAccess);
            map.put("refresh", newRefresh);

            // 기존에 저장되어있는 refresh token을 삭제한 후 새로운 refresh token을 저장
            redisHandler.delete(accessToken);

            addRefreshToken(newAccess, newRefresh);

            return map;
        } catch (Exception e) {
            log.error("Token 재발급 중 오류 발생", e);
            throw new JwtTokenException(TOKEN_REISSUE_FAILED);
        }
    }

    // Redis에 access token이 저장되어 있는지 확인
    @Override
    public void isAccessTokenExist(String accessToken) {
        boolean isExist = redisHandler.exists(accessToken);
        if (!isExist) {
            throw new JwtTokenException(ACCESS_TOKEN_NOT_FOUND);
        }
    }

    // refresh token 저장
    private void addRefreshToken(String access, String refresh) {

        redisHandler.save(access, refresh, REFRESH_TOKEN_EXPIRE_TIME);
    }

}
