package com.nemo.oceanAcademy.domain.auth.application.controller;

import com.nemo.oceanAcademy.common.response.ApiResponse;
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

/**
 * OAuth2AuthController는 카카오 OAuth2 인증 및 회원가입/탈퇴 관련 API를 처리합니다.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OAuth2AuthController {

    private final OAuth2AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoConfig kakaoConfig;

    /**
     * 클라이언트에게 카카오 앱 키 발급
     * @return 카카오 앱 키
     */
    @GetMapping("/kakao/app-key")
    public ResponseEntity<?> getKakaoAppKey() {
        Map<String, String> response = new HashMap<>();
        response.put("appKey", kakaoConfig.getKakaoClientId());
        return ApiResponse.success("카카오 앱 키 발급 성공", "Kakao app key retrieved successfully", response);
    }

    /**
     * 카카오 인증 코드 처리 후 JWT 발급
     * @param code 카카오 인증 코드
     * @return JWT 액세스 토큰 및 리프레시 토큰
     */
    @GetMapping("/kakao/callback")
    public ResponseEntity<?> kakaoLogin(@RequestParam("code") String code) {
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

        return ApiResponse.success("JWT 발급 성공", "JWT tokens issued successfully", tokens);
    }

    /**
     * 회원가입 여부 확인
     * @param request 인증된 사용자 요청 객체 (JWT에서 userId 추출)
     * @return 회원가입 여부 결과
     */
    @GetMapping("/signup")
    public ResponseEntity<?> checkSignup(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        authService.checkSignup(userId);  // 예외가 발생하지 않으면 회원임을 의미
        return ApiResponse.success("가입된 회원입니다.", "Existing member", null);
    }

    /**
     * 회원가입 신청
     * @param request 인증된 사용자 요청 객체
     * @param nickname 사용자 닉네임
     * @param file 프로필 이미지 파일 (선택)
     * @return 회원가입 결과
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(HttpServletRequest request,
                                    @RequestParam("nickname") String nickname,
                                    @RequestPart(value = "file", required = false) MultipartFile file) {
        String userId = (String) request.getAttribute("userId");
        authService.signup(userId, nickname, file); // 서비스에서 예외 처리
        return ApiResponse.success("회원가입 완료", "Signup successful", null);
    }

    /**
     * 회원탈퇴 신청 (soft delete)
     * @param request 인증된 사용자 요청 객체
     * @return 회원탈퇴 결과
     */
    @DeleteMapping("/signup")
    public ResponseEntity<?> withdraw(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        authService.withdraw(userId); // 서비스에서 예외 처리
        return ApiResponse.success("회원탈퇴 완료", "Withdrawal successful", null);
    }
}
