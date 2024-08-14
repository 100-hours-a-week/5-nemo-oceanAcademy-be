package com.nemo.oceanAcademy.domain.user.controller;

import com.nemo.oceanAcademy.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 닉네임 중복 검사
    @GetMapping("/checkNickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
        boolean exists = userService.isNicknameTaken(nickname);
        return ResponseEntity.ok(exists);
    }

    // 회원탈퇴 (소프트 삭제)
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser() {
        userService.deleteCurrentUser();
        return ResponseEntity.noContent().build();
    }
}
