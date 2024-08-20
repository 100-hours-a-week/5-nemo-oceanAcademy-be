package com.nemo.oceanAcademy.auth.application.controller;

import com.nemo.oceanAcademy.auth.application.service.OAuth2AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
public class OAuth2AuthController {

    private final OAuth2AuthService authService;

    @Autowired
    public OAuth2AuthController(OAuth2AuthService authService) {
        this.authService = authService;
    }

    // 회원가입 여부 확인
    @GetMapping("/signup")
    public ResponseEntity<String> checkSignup(@RequestParam("user_id") String userId) {
        boolean isSignedUp = authService.isUserSignedUp(userId);
        if (isSignedUp) {
            return ResponseEntity.ok("이미 가입된 회원입니다.");
        }
        return ResponseEntity.status(204).body("회원 기록이 없습니다");
    }

    // OAuth2 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestParam("user_id") String userId,
                                         @RequestParam("nickname") String nickname,
                                         @RequestPart(value = "file", required = false) MultipartFile file) {
        boolean isCreated = authService.signupUser(userId, nickname, file);
        if (isCreated) {
            return ResponseEntity.status(201).body("회원가입이 완료되었습니다.");
        }
        return ResponseEntity.status(400).body("회원가입에 실패하였습니다.");
    }

    // Soft Delete로 회원 탈퇴 (회원 탈퇴 상태로 수정)
    @PatchMapping("/signup")
    public ResponseEntity<String> withdraw(@RequestParam("user_id") String userId) {
        boolean isDeleted = authService.softDeleteUser(userId);
        if (isDeleted) {
            return ResponseEntity.ok("회원탈퇴가 완료되었습니다.");
        }
        return ResponseEntity.status(400).body("회원탈퇴에 실패하였습니다.");
    }
}
