package com.nemo.oceanAcademy.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtTokenProvider {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // 안전한 키 생성

    private final long accessTokenValidity = 3600000; // 1시간
    private final long refreshTokenValidity = 604800000; // 7일

    private final UserDetailsService userDetailsService; // 인증에 사용되는 UserDetailsService

    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // Authentication 객체에서 JWT 토큰 생성
    public String createToken(Authentication authentication) {
        String userId = authentication.getName(); // 사용자 ID
        Claims claims = Jwts.claims().setSubject(userId); // 사용자 ID를 클레임에 포함

        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValidity); // 토큰 만료 시간

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Access Token 생성
    public String createAccessToken(String userId) {
        Claims claims = Jwts.claims().setSubject(userId); // 사용자 ID를 클레임에 포함
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValidity);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Refresh Token 생성
    public String createRefreshToken(String userId) {
        Claims claims = Jwts.claims().setSubject(userId); // 사용자 ID를 클레임에 포함
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenValidity);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Refresh Token으로 새로운 Access Token 발급
    public String refreshAccessToken(String refreshToken) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();

        String userId = claims.getSubject(); // 사용자 ID 추출

        // 새로운 Access Token 발급
        return createAccessToken(userId);
    }

    // JWT 토큰에서 인증 정보 가져오기
    public Authentication getAuthentication(String token) {
        String userId = getUsernameFromToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // JWT 토큰에서 사용자 ID 추출
    private String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    // 요청 헤더에서 토큰 가져오기
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 토큰의 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
