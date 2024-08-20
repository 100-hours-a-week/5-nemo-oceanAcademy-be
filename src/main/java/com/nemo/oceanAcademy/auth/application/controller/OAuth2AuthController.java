package com.nemo.oceanAcademy.auth.application.controller;

import com.nemo.oceanAcademy.auth.application.dto.TokenResponseDTO;
import com.nemo.oceanAcademy.auth.application.service.OAuth2AuthService;
import com.nemo.oceanAcademy.auth.security.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
public class OAuth2AuthController {

    private final OAuth2AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    public OAuth2AuthController(OAuth2AuthService authService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 회원가입 여부 확인
    @GetMapping("/signup")
    public ResponseEntity<String> checkSignup(@RequestParam("user_id") String userId) {
        boolean isSignedUp = authService.isUserSignedUp(userId);
        if (isSignedUp) {
            return ResponseEntity.ok("이미 가입된 회원입니다.");
        }
        return ResponseEntity.noContent().build(); // 가입되지 않은 회원이면 204 No Content
    }

    // 회원가입 및 토큰 발급
    @PostMapping("/signup")
    public ResponseEntity<TokenResponseDTO> signup(
            @RequestParam("user_id") String userId,
            @RequestParam("nickname") String nickname,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        TokenResponseDTO tokenResponse = authService.signupUser(userId, nickname, file);
        return ResponseEntity.status(201).body(tokenResponse);
    }

    // Access Token 갱신
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDTO> refreshAccessToken(@RequestParam("refreshToken") String refreshToken) {
        String newAccessToken = jwtTokenProvider.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(new TokenResponseDTO(newAccessToken, refreshToken));
    }

    // 회원 탈퇴
    @DeleteMapping("/signup")
    public ResponseEntity<String> withdraw(@RequestParam("user_id") String userId) {
        boolean isDeleted = authService.softDeleteUser(userId);
        if (isDeleted) {
            return ResponseEntity.ok("회원탈퇴가 완료되었습니다.");
        }
        return ResponseEntity.status(400).body("회원탈퇴에 실패하였습니다.");
    }
}
