package com.nemo.oceanAcademy.domain.auth.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    // 1주일에 해당하는 밀리초 (7일 * 24시간 * 60분 * 60초 * 1000밀리초)
    private final long expirationTime = 7 * 24 * 60 * 60 * 1000;

    // JWT 생성
    public String createToken(Long userId) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    // JWT에서 사용자 ID 추출
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    // JWT 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // 토큰이 만료되었거나, 변조되었을 경우 예외 처리
            return false;
        }
    }

    // JWT 무효화 (토큰을 명시적으로 무효화하려면 별도의 저장소나 블랙리스트 관리가 필요합니다)
    public void invalidateToken(Long userId) {
        // JWT는 자체적으로 무효화가 어렵기 때문에 별도의 구현이 필요합니다.
        // 예를 들어, Redis를 이용한 블랙리스트 관리, 토큰 만료 시간 조정 등이 있습니다.
    }
}
