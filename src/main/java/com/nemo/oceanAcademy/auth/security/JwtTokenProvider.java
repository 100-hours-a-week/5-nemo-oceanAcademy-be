package com.nemo.oceanAcademy.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

    // SecretKey 생성 (256비트 이상의 안전한 키)
    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long validityInMilliseconds = 3600000; // 1시간

    // JWT 토큰 생성
    public String createToken(String userId) {
        Claims claims = Jwts.claims().setSubject(userId);
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey)  // SecretKey 객체를 사용
                .compact();
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(String userId) {
        long refreshValidityInMilliseconds = validityInMilliseconds * 24 * 7; // 일주일
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey)  // SecretKey 객체를 사용
                .compact();
    }

    // JWT에서 사용자 ID 추출
    public String getUserId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)  // 동일한 secretKey로 서명 검증
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (SignatureException e) {
            System.out.println("Invalid JWT signature: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("JWT parsing error: " + e.getMessage());
            throw e;
        }
    }


    // JWT에서 사용자 ID 추출 (getUserIdFromToken 이름으로 추가)
    public String getUserIdFromToken(String token) {
        return getUserId(token); // 기존 getUserId 메소드를 호출
    }

    // JWT 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)  // SecretKey로 서명 검증
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("JWT token is expired: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("JWT token validation failed: " + e.getMessage());
            return false;
        }
    }
}
