package com.nemo.oceanAcademy.domain.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
        String token = resolveToken(request);                             // 토큰 가져오기
        if (token != null && jwtTokenProvider.validateToken(token)) {     // 토큰 유효성 검증
            String userId = jwtTokenProvider.getUserId(token);            // 토큰에서 사용자 ID 추출
            request.setAttribute("userId", userId);                 // 사용자 ID를 요청에 설정

            System.out.println("Extracted token: " + token);
            System.out.println("User ID from token: " + userId);
        }
        filterChain.doFilter(request, response);  // 필터 체인 통과
    }


    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
