package com.nemo.oceanAcademy.domain.auth.application.controller;
import com.nemo.oceanAcademy.domain.auth.application.service.OAuth2AuthService;
import com.nemo.oceanAcademy.domain.auth.security.JwtTokenProvider;
import com.nemo.oceanAcademy.config.KakaoConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
/*
    /api/auth/kakao/app-key
        Get - 클라이언트에게 카카오 앱 키 발급

    /api/auth/kakao/callback
        Get - 카카오 인증 코드 처리 후 JWT 발급

    /api/auth/signup
        Get - 회원가입 여부 확인
        Post - 회원가입 신청
        Patch - 회원탈퇴 신청
*/

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OAuth2AuthController {

    private final OAuth2AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoConfig kakaoConfig;

    // 클라이언트에게 카카오 앱 키 발급 (Rest Api App key)
    @GetMapping("/kakao/app-key")
    public ResponseEntity<Map<String, String>> getKakaoAppKey() {
        Map<String, String> response = new HashMap<>();
        response.put("appKey", kakaoConfig.getKakaoClientId());
        return ResponseEntity.ok(response);
    }

    // 카카오 인증 코드 처리 후 JWT 발급
    @GetMapping("/kakao/callback")
    public ResponseEntity<Map<String, String>> kakaoLogin(@RequestParam("code") String code) {
        String kakaoAccessToken = authService.getKakaoAccessToken(code);
        Map<String, Object> kakaoUserInfo = authService.getKakaoUserInfo(kakaoAccessToken);

        // 사용자 식별값 추출
        String userId = (String) kakaoUserInfo.get("id");

        // JWT 액세스 토큰, 리프레시 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(userId);
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return ResponseEntity.ok(tokens);
    }

    // 회원가입 여부 확인
    @GetMapping("/signup")
    public ResponseEntity<?> checkSignup(HttpServletRequest request) {

        // JwtAuthenticationFilter에 userId를 요청하여 받아옴 - JWT AccessToken에서 추출
        String userId = (String) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized"); // 검증 - 인증 안됨
        }
        return authService.checkSignup(userId); // 회원가입 여부 확인, 요청 결과 반환
    }

    // 회원가입 신청 - 정보는 파라미터가 아니라 바디에서 받아와야함, 프로필 이미지는 폼 데이터에서 받아와야함
    @PostMapping("/signup")
    public ResponseEntity<?> signup(HttpServletRequest request,
                                    @RequestParam("nickname") String nickname,
                                    @RequestPart(value = "file", required = false) MultipartFile file) {

        // JwtAuthenticationFilter에 userId를 요청하여 받아옴 - JWT AccessToken에서 추출
        String userId = (String) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized"); // 검증 - 인증 안됨
        }
        return authService.signup(userId, nickname, file); // 회원가입 진행, 요청 결과 반환
    }

    // TODO : 회원탈퇴 신청 - soft delete - 성공 / 이슈
    @DeleteMapping("/signup")
    public ResponseEntity<?> withdraw(HttpServletRequest request) {

        // JwtAuthenticationFilter에 userId를 요청하여 받아옴 - JWT AccessToken에서 추출
        String userId = (String) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized"); // 검증 - 인증 안됨
        }
        return authService.withdraw(userId); // 회원탈퇴 진행, 요청 결과 반환
    }
}
