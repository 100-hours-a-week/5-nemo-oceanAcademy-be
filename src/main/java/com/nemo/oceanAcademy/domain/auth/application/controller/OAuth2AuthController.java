package com.nemo.oceanAcademy.domain.auth.application.controller;

import com.nemo.oceanAcademy.domain.auth.application.service.OAuth2AuthService;
import com.nemo.oceanAcademy.domain.auth.security.JwtTokenProvider;
import com.nemo.oceanAcademy.config.KakaoConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class OAuth2AuthController {

    private final OAuth2AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoConfig kakaoConfig;  // KakaoConfig 주입받기

    // KakaoConfig을 생성자에 추가
    public OAuth2AuthController(OAuth2AuthService authService, JwtTokenProvider jwtTokenProvider, KakaoConfig kakaoConfig) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.kakaoConfig = kakaoConfig;
    }

    // 카카오 앱 키 제공
    @GetMapping("/kakao/app-key")
    public ResponseEntity<Map<String, String>> getKakaoAppKey() {
        // KakaoConfig에서 clientId를 가져와서 응답
        Map<String, String> response = new HashMap<>();
        response.put("appKey", kakaoConfig.getKakaoClientId());
        return ResponseEntity.ok(response); // JSON 응답으로 수정
    }

    // 카카오 인증 코드 처리 후 JWT 발급
    @GetMapping("/kakao/callback")
    public ResponseEntity<Map<String, String>> kakaoLogin(@RequestParam("code") String code) {
        String kakaoAccessToken = authService.getKakaoAccessToken(code);
        Map<String, Object> kakaoUserInfo = authService.getKakaoUserInfo(kakaoAccessToken);

        String userId = (String) kakaoUserInfo.get("id");
        String accessToken = jwtTokenProvider.createToken(userId); // JWT 액세스 토큰 생성
        String refreshToken = jwtTokenProvider.createRefreshToken(userId); // JWT 리프레시 토큰 생성

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return ResponseEntity.ok(tokens); // JSON 응답으로 토큰 반환
    }

    // 회원가입 여부 확인
    @GetMapping("/signup")
    public ResponseEntity<?> checkSignup(@RequestHeader("Authorization") String token) {
        // Bearer 토큰에서 'Bearer ' 부분을 제거하고 JWT 토큰만 추출
        String bearerToken = extractToken(token);
        String userId = jwtTokenProvider.getUserId(bearerToken);

        // 회원가입 여부 확인 후 결과 반환
        return authService.checkSignup(userId);
    }

    // 회원가입 신청
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestHeader("Authorization") String token,
                                    @RequestParam("nickname") String nickname,
                                    @RequestPart(value = "file", required = false) MultipartFile file) {
        // 'Bearer ' 부분을 제거하고 실제 JWT 토큰을 추출
        String bearerToken = extractToken(token);
        String userId = jwtTokenProvider.getUserId(bearerToken);

        // 회원가입 처리 후 결과 반환
        return authService.signup(userId, nickname, file);
    }

    // Bearer 토큰에서 "Bearer " 부분 제거하는 메서드
    private String extractToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7).trim(); // "Bearer " 제거 후 반환
        }
        return null;
    }
}
