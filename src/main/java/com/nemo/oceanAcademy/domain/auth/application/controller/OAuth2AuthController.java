package com.nemo.oceanAcademy.domain.auth.application.controller;
import com.nemo.oceanAcademy.common.exception.UnauthorizedException;
import com.nemo.oceanAcademy.common.response.ApiResponse;
import com.nemo.oceanAcademy.domain.auth.application.dto.SignupRequestDto;
import com.nemo.oceanAcademy.domain.auth.application.service.OAuth2AuthService;
import com.nemo.oceanAcademy.domain.auth.security.JwtTokenProvider;
import com.nemo.oceanAcademy.config.KakaoConfig;
import com.nemo.oceanAcademy.domain.user.application.dto.UserUpdateDTO;
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
    public ResponseEntity<Map<String, String>> getKakaoAppKey() {
        Map<String, String> response = new HashMap<>();
        response.put("appKey", kakaoConfig.getKakaoClientId());
        return ResponseEntity.ok(response);
    }

    /**
     * 카카오 인증 코드 처리 후 JWT 발급
     * @param code 카카오 인증 코드
     * @return JWT 액세스 토큰 및 리프레시 토큰
     */
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

    /* ----------------------------------------------------------------- */

    /**
     * 공통 사용자 인증 처리 메서드 - request에서 userId를 추출해 인증된 사용자 ID를 반환
     * @param request HttpServletRequest 객체로부터 userId 추출
     * @return userId 인증된 사용자 ID
     * @throws UnauthorizedException 사용자 ID가 없을 경우 예외 발생
     */
    private String getAuthenticatedUserId(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        if (userId == null) {
            throw new UnauthorizedException(
                    "사용자 인증에 실패했습니다. (토큰 없음)",
                    "Unauthorized request: userId not found"
            );
        }
        return userId;
    }

    /**
     * 회원가입 여부 확인
     * @param request 인증된 사용자 요청 객체
     * @return 회원가입 여부 결과
     */
    @GetMapping("/signup")
    public ResponseEntity<?> checkSignup(HttpServletRequest request) {
        String userId = getAuthenticatedUserId(request);
        authService.checkSignup(userId);
        return ApiResponse.success("가입된 회원입니다.", "Existing member", null);
    }

    /**
     * 회원가입 신청
     * @param request 인증된 사용자 요청 객체
     * @param signupRequestDto 사용자 닉네임
     * @param imagefile 프로필 이미지 파일 (선택)
     * @return 회원가입 결과
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(HttpServletRequest request,
                                    @RequestPart("signupRequestDto") SignupRequestDto signupRequestDto,
                                    @RequestPart(value = "imagefile", required = false) MultipartFile imagefile) {

        authService.signup(request, signupRequestDto, imagefile);
        return ApiResponse.success("회원가입이 완료되었습니다.", "Signup successful", null);
    }

    /**
     * 회원탈퇴 신청 (soft delete)
     * @param request 인증된 사용자 요청 객체
     * @return 회원탈퇴 결과
     */
    @DeleteMapping("/signup")
    public ResponseEntity<?> withdraw(HttpServletRequest request) {
        String userId = getAuthenticatedUserId(request);
        authService.withdraw(userId);
        return ApiResponse.success("회원탈퇴가 완료되었습니다.", "Withdrawal successful", null);
    }
}
