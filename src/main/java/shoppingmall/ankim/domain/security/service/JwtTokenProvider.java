package shoppingmall.ankim.domain.security.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import shoppingmall.ankim.domain.security.dto.CustomUserDetails;
import shoppingmall.ankim.domain.security.exception.JwtTokenException;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import static shoppingmall.ankim.global.exception.ErrorCode.*;
import static shoppingmall.ankim.global.exception.ErrorCode.EXPIRED_JWT_TOKEN;
import static shoppingmall.ankim.global.exception.ErrorCode.INVALID_JWT_SIGNATURE;

// Jwt 토큰 생성, 인증, 권한 부여, 유효성 검사, pk 추출 등 기능 제공
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access.token.expire.time}")
    private long ACCESS_TOKEN_EXPIRE_TIME;

    @Value("${jwt.refresh.token.expire.time}")
    private long REFRESH_TOKEN_EXPIRE_TIME;

    // 토큰 생성 메서드
    public String generateToken(CustomUserDetails userDetails, String category, long expirationTime) {
        Date now = new Date(); // 생성일 설정
        return Jwts.builder()
                .subject(userDetails.getUsername()) // loginId 저장
                .claim("category", category)
                .claim("name", userDetails.getNickName()) // 사용자 이름 저장
                .claim("roles", userDetails.getAuthorities()) // 권한 정보 저장
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationTime))
                .signWith(getSecretKey(secretKey))
                .compact();
    }

    // secrete key 생성
    private Key getSecretKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Access Token 생성
    public String generateAccessToken(CustomUserDetails userDetails, String category) {
        return generateToken(userDetails, category, ACCESS_TOKEN_EXPIRE_TIME);
    }

    // Refresh Token 생성
    public String generateRefreshToken(CustomUserDetails userDetails, String category) {
        return generateToken(userDetails, category, REFRESH_TOKEN_EXPIRE_TIME);
    }
    public String generateRefreshToken(CustomUserDetails userDetails, String category, long expireTime) {
        return generateToken(userDetails, category, expireTime);
    }

    // Token 유효 여부 검증
    public void isTokenValidate(String token) {
        try {
            Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseSignedClaims(token);
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
            throw new JwtTokenException(INVALID_JWT_SIGNATURE);
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
            throw new JwtTokenException(UNSUPPORTED_JWT_TOKEN);
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.");
            throw new JwtTokenException(INVALID_JWT_TOKEN);
        }
    }

    // Token 만료 여부 검증
    public boolean isTokenExpired(String token) {
            Date expirationDate = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();
            return expirationDate.before(new Date()); // expirationDate가 현재 시간보다 이전인지 확인(true:만료, false:유효)
    }

    // 토큰에서 사용자 이름(loginId) 추출
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject(); // Subject는 username(loginId)
    }

    // 토큰에서 Category 추출
    public String getCategoryFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // 클레임에서 "category" 값을 추출
        return claims.get("category", String.class);
    }

    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // 클레임에서 "category" 값을 추출
        return claims.get("roles", String.class);
    }
}
