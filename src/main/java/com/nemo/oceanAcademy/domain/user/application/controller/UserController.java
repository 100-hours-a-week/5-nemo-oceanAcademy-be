package com.nemo.oceanAcademy.domain.user.application.controller;
import com.nemo.oceanAcademy.domain.user.application.dto.UserResponseDTO;
import com.nemo.oceanAcademy.domain.user.application.dto.UserUpdateDTO;
import com.nemo.oceanAcademy.domain.user.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
/*
    /api/users
        Get - 사용자 정보 조회
        Patch - 사용자 정보 업데이트

    /api/users/checkNickname
        Get 닉네임 중복 검사
*/

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // TODO : 사용자 정보 조회
    @GetMapping
    public ResponseEntity<UserResponseDTO> getUserInfo(HttpServletRequest request) {
        // 인증: 사용자 ID - From JwtAuthenticationFilter
        UserResponseDTO userDTO = userService.getUserInfo(request);
        if (userDTO != null) {
            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }

    // TODO : 사용자 정보 업데이트
    @PatchMapping
    public ResponseEntity<String> updateUserProfile(HttpServletRequest request,
                                                    @RequestBody UserUpdateDTO userUpdateDTO,
                                                    @RequestPart(value = "file", required = false) MultipartFile file) {
        // 인증: 사용자 ID - From JwtAuthenticationFilter
        userService.updateUserProfile(request, userUpdateDTO, file);
        return ResponseEntity.ok("회원 정보가 수정되었습니다.");
    }

    // TODO : 닉네임 중복 확인
    @GetMapping("/checkNickname")
    public ResponseEntity<String> checkNickname(@RequestParam("nickname") String nickname) {
        boolean isAvailable = userService.isNicknameAvailable(nickname);
        if (isAvailable) {
            return ResponseEntity.ok("사용 가능한 닉네임입니다.");
        } else {
            return ResponseEntity.status(409).body("중복된 닉네임입니다.");
        }
    }
}
