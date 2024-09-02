package com.nemo.oceanAcademy.domain.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private static final Logger logger = Logger.getLogger(JwtTokenProvider.class.getName());

    public JwtTokenProvider(@Value("${secret.key}") String secretKeyString) {
        // 주입된 문자열을 Base64로 디코딩하여 SecretKey 생성
        byte[] decodedKey = Base64.getDecoder().decode(secretKeyString);
        this.secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    private final long validityInMilliseconds = 3600000; // 한 시간

    // JWT 토큰 생성
    public String createAccessToken(String userId) {
        Claims claims = Jwts.claims().setSubject(userId);
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds); // 한 시간

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(String userId) {
        long refreshValidityInMilliseconds = validityInMilliseconds * 24 * 7; // 토큰 유효 기간 일주일
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
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
            logger.warning("Expired JWT token");
            return false;
        } catch (Exception e) {
            logger.warning("Invalid JWT token: " + e.getMessage());
            return false;
        }
    }

    // JWT 토큰에서 UserId 추출
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();  // UserId 추출
    }

    // 토큰으로 Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // roles 필드가 null일 경우 빈 리스트로 처리 (본 서비스에서는 role 안함)
        String roles = (String) claims.get("roles");
        List<GrantedAuthority> authorities = (roles != null) ?
                Arrays.stream(roles.split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()) :
                List.of();  // 빈 리스트

        // 사용자 정보 설정
        MemberInfo memberInfo = new MemberInfo();
        memberInfo.setAuthorities(authorities);         // 권한 설정
        memberInfo.setUserId(claims.getSubject());      // 사용자 ID 설정

        // UsernamePasswordAuthenticationToken에서 비밀번호 부분은 null
        return new UsernamePasswordAuthenticationToken(memberInfo, null, authorities);
    }

    // Authentication 객체를 SecurityContext에 설정
    public void setAuthentication(String token) {
        Authentication authentication = getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    //stomp authorization 추출
    public String extractJwt(StompHeaderAccessor accessor) {
        return accessor.getFirstNativeHeader("Authorization");
    }

}
