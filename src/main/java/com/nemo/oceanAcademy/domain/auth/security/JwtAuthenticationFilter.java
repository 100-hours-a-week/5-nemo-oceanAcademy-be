package com.nemo.oceanAcademy.domain.auth.security;
import io.jsonwebtoken.ExpiredJwtException;
import io.sentry.Sentry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nemo.oceanAcademy.common.exception.JwtAuthenticationException;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // 토큰 유효성 확인 및 사용자 정보 추출
                String userId = jwtTokenProvider.getUserIdFromToken(token);
                logger.info("User ID from token: {}", userId);

                request.setAttribute("userId", userId);
                jwtTokenProvider.setAuthentication(token);
            }
        } catch (ExpiredJwtException e) {
            // AccessToken이 만료된 경우
            logger.info("Access token expired, checking refresh token");
            String refreshToken = resolveRefreshToken(request);

            try {
                if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
                    String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
                    String newAccessToken = jwtTokenProvider.createAccessToken(userId);

                    // 새로운 accessToken을 응답 헤더에 추가
                    response.setHeader("Authorization", "Bearer " + newAccessToken);

                    // 사용자 정보를 request에 설정 및 SecurityContext에 설정
                    request.setAttribute("userId", userId);
                    jwtTokenProvider.setAuthentication(newAccessToken);

                    logger.info("New access token generated and set in response header.");
                } else {
                    throw new JwtAuthenticationException(
                            "리프레시 토큰이 유효하지 않거나 만료되었습니다.",
                            "Refresh token is invalid or expired."
                    );
                }
            } catch (Exception ex) {
                Sentry.captureException(ex);
                throw new JwtAuthenticationException(
                        "리프레시 토큰 처리 중 오류가 발생했습니다.",
                        "Error processing refresh token."
                );
            }
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new JwtAuthenticationException(
                    "JWT 처리 중 예상치 못한 오류가 발생했습니다.",
                    "Unexpected error occurred during JWT processing."
            );
        }

        filterChain.doFilter(request, response);

        logger.info("Request processed successfully: {}", request.getRequestURI());
        logger.info("Response status code: {}", response.getStatus());
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private String resolveRefreshToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Refresh-Token");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
