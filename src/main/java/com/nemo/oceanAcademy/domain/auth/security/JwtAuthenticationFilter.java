package com.nemo.oceanAcademy.domain.auth.security;
import com.nemo.oceanAcademy.domain.user.application.controller.UserController;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.logging.Logger;

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

        // 필터 체인 이어나가기
        filterChain.doFilter(request, response);
        System.out.println(response);

        // 필터가 정상적으로 통과된 후
        System.out.println("필터 통과 후 요청 처리 완료: " + request.getRequestURI());
        System.out.println("응답 상태 코드: " + response.getStatus());
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
