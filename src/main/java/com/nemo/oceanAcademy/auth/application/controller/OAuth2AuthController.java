package com.nemo.oceanAcademy.auth.application.controller;

import com.nemo.oceanAcademy.auth.application.service.OAuth2AuthService;
import com.nemo.oceanAcademy.auth.security.JwtTokenProvider;
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
    public ResponseEntity<?> getKakaoAppKey() {
        // KakaoConfig에서 clientId를 가져와서 응답
        return ResponseEntity.ok("{\"appKey\": \"" + kakaoConfig.getKakaoClientId() + "\"}");
    }

    // 카카오 인증 코드 처리 후 JWT 발급
    @GetMapping("/kakao/callback")
    public ResponseEntity<Map<String, String>> kakaoLogin(@RequestParam("code") String code) {
        String kakaoAccessToken = authService.getKakaoAccessToken(code);
        Map<String, Object> kakaoUserInfo = authService.getKakaoUserInfo(kakaoAccessToken);

        String userId = (String) kakaoUserInfo.get("id");
        String accessToken = jwtTokenProvider.createToken(userId);
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);
        System.out.println("accessToken :"+ accessToken);
        System.out.println("refreshToken :"+ refreshToken);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return ResponseEntity.ok(tokens);
    }

    // 회원가입 여부 확인
    @GetMapping("/signup")
    public ResponseEntity<?> checkSignup(@RequestHeader("Authorization") String token) {
        // Bearer 토큰에서 앞의 'Bearer ' 부분을 제거하고 JWT 토큰만 추출
        String userId = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));

        // 회원가입 여부를 확인하는 메소드를 호출하여 ResponseEntity 반환
        return authService.checkSignup(userId);
    }

    // 회원가입 신청
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestHeader("Authorization") String token,
                                    @RequestParam("nickname") String nickname,
                                    @RequestPart(value = "file", required = false) MultipartFile file) {
        // 'Bearer ' 부분을 제거하고 실제 JWT 토큰을 추출
        String actualToken = token.replace("Bearer ", "").trim();
        String userId = jwtTokenProvider.getUserIdFromToken(actualToken); // 토큰에서 userId 추출
        return authService.signup(userId, nickname, file);
    }
}
