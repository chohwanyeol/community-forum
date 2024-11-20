package com.mysite.sbb.token;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    //고정키
    private final String secret = "my-very-secure-secret-key-1234567890abcdef";
    private final SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes());

    private final long validityInMilliseconds = 1000L * 60 * 60 * 24 * 30 * 3; // 토큰 유효 기간 (3개월)

    // JWT 토큰 생성
    public String generateToken(String username) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(username)  // 사용자 이름 설정
                .setIssuedAt(new Date(now))  // 발급 시간 설정
                .setExpiration(validity)  // 만료 시간 설정
                .signWith(secretKey)  // 안전한 키로 서명
                .compact();  // 토큰 생성
    }

    // JWT 토큰에서 사용자 정보 추출
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)  // 안전한 키로 토큰 파싱
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();  // 사용자 이름 반환
    }

    // JWT 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)  // 안전한 키로 검증
                    .build()
                    .parseClaimsJws(token);

            return true;  // 토큰이 유효하면 true 반환
        } catch (Exception e) {
            return false;  // 토큰이 유효하지 않으면 false 반환
        }
    }
}
