package com.nemo.oceanAcademy.domain.auth.controller;

import com.nemo.oceanAcademy.domain.auth.dto.AuthDto;
import com.nemo.oceanAcademy.domain.user.entity.User;
import com.nemo.oceanAcademy.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    @Autowired
    public AuthController(UserService userService, JwtProvider jwtProvider) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
    }

    // 회원가입 여부 확인 (OAuth 로그인 후)
    @GetMapping("/signup")
    public ResponseEntity<Boolean> checkSignupStatus() {
        Long userId = userService.getCurrentUserId();
        boolean isSignedUp = userService.isUserSignedUp(userId);
        return ResponseEntity.ok(isSignedUp);
    }

    // 회원가입 요청 (OAuth 로그인 후 회원정보 저장)
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody AuthDto AuthDto) {
        Long oauthId = AuthDto.getOauthId();
        User user = User.builder()
                        .id(oauthId)
                        .nickname(AuthDto.getNickname())
                        .email(AuthDto.getEmail())
                        .profileImagePath(AuthDto.getProfileImagePath())
                        .build();

        userService.createUser(user);

        // JWT 생성 및 반환
        String jwtToken = jwtProvider.createToken(oauthId);
        return ResponseEntity.ok(jwtToken);
    }

    // OAuth 로그아웃 및 토큰 무효화
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        Long userId = userService.getCurrentUserId();
        jwtProvider.invalidateToken(userId);
        return ResponseEntity.noContent().build();
    }
}