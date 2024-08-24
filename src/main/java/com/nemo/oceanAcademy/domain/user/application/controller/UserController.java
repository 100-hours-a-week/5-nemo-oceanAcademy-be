package com.nemo.oceanAcademy.domain.user.application.controller;
import com.nemo.oceanAcademy.domain.schedule.application.dto.ScheduleDto;
import com.nemo.oceanAcademy.domain.user.application.dto.UserResponseDTO;
import com.nemo.oceanAcademy.domain.user.application.dto.UserUpdateDTO;
import com.nemo.oceanAcademy.domain.user.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
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

    // TODO : 사용자 정보 조회 - 성공
    @GetMapping
    public ResponseEntity<UserResponseDTO> getUserInfo(HttpServletRequest request) {
        System.out.println("오아아");
        // JWT에서 추출한 사용자 ID 가져오기
        String userId = (String) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(null); // 사용자 ID가 없을 때 401 Unauthorized 반환
        }

        // 사용자 정보 조회
        System.out.println(userId);
        UserResponseDTO userResponseDTO = userService.getUserInfo(userId);
        if (userResponseDTO != null) {
            return ResponseEntity.ok(userResponseDTO); // 사용자 정보가 있으면 200 OK와 함께 반환
        } else {
            return ResponseEntity.status(404).body(null); // 사용자 정보가 없으면 404 Not Found 반환
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

    // TODO : 닉네임 중복 확인  - 성공
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
