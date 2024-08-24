package com.nemo.oceanAcademy.domain.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 필터 체인
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 토큰 가져오기
        String token = resolveToken(request);

        // 토큰 유효성 검증
        if (token != null && jwtTokenProvider.validateToken(token)) {

            // UserId 추출
            String userId = jwtTokenProvider.getUserIdFromToken(token);
            logger.info("User ID from token: {}", userId);

            // request에 userId 추가
            request.setAttribute("userId", userId);
            logger.info("User ID set in request attribute: {}", userId);

            // SecurityContext에 Authentication 설정
            jwtTokenProvider.setAuthentication(token);

            // 토큰 및 사용자 정보 출력
            logger.info("Extracted token: {}", token);
            logger.info("User ID from SecurityContext: {}", SecurityContextHolder.getContext().getAuthentication().getName());
        }

        // 필터 체인 계속..
        filterChain.doFilter(request, response);

        // 필터가 정상적으로 통과된 후
        logger.info("필터 통과 후 요청 처리 완료: {}", request.getRequestURI());
        logger.info("응답 상태 코드: {}", response.getStatus());
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
