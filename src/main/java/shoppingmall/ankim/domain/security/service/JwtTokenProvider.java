package shoppingmall.ankim.domain.security.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import shoppingmall.ankim.domain.security.dto.CustomUserDetails;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

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
                .claim("name", userDetails.getUsername()) // 사용자 이름 저장
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

    // Token 유효 여부 검증
    public boolean isTokenValidate(String token) {
        try {
            // SecretKey를 사용하여 JWT를 파싱하고 검증
            Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8))) // SecretKey 생성 및 설정
                    .build()
                    .parseSignedClaims(token); // 토큰 파싱 및 검증
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false; // 유효하지 않은 경우 false 반환
    }

    // Token 만료 여부 검증
    public boolean isTokenExpired(String token) {
            Date expirationDate = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();
            return expirationDate.before(new Date()); // expirationDate가 현재 시간보다 이전인지 확인
    }

    // refreshToken이 만료되지 않은 Token 정보 추출


    // refreshToken이 만료된 Token 정보 추출


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
