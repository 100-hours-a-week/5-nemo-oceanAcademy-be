package com.nemo.oceanAcademy.domain.auth.security;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 토큰 가져오기
        String token = resolveToken(request);

        // 토큰 유효성 검증
        if (token != null && jwtTokenProvider.validateToken(token)) {

            // JWT 내 사용자 ID 추출
            String userId = getUserIdFromToken(token);

            // 사용자 ID를 요청에 설정
            request.setAttribute("userId", userId);

            // 콘솔 확인
            System.out.println("Extracted token: " + token);
            System.out.println("User ID from token: " + userId);
        }

        // 필터 체인 통과
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // JWT 토큰에서 사용자 ID 직접 추출
    private String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtTokenProvider.getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Subject에 저장된 사용자 ID 추출
        return claims.getSubject();
    }
}
