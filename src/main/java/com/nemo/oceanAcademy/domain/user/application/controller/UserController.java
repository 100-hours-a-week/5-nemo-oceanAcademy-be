package com.nemo.oceanAcademy.domain.user.application.controller;
import com.nemo.oceanAcademy.common.exception.UnauthorizedException;
import com.nemo.oceanAcademy.common.response.ApiResponse;
import com.nemo.oceanAcademy.common.response.ErrorResponse;
import com.nemo.oceanAcademy.domain.user.application.dto.UserResponseDTO;
import com.nemo.oceanAcademy.domain.user.application.dto.UserUpdateDTO;
import com.nemo.oceanAcademy.domain.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;

/**
 * UserController는 사용자 정보 조회, 업데이트, 닉네임 중복 확인 등의 API를 처리합니다.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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
     * 사용자 정보 조회
     * @param request 인증된 사용자 요청 객체 (JWT에서 userId 추출)
     * @return ResponseEntity<UserResponseDTO> 사용자 정보
     */
    @GetMapping
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
        String userId = getAuthenticatedUserId(request);
        UserResponseDTO userResponseDTO = userService.getUserInfo(userId);

        return ApiResponse.success("사용자 정보 조회 성공", "User info retrieved successfully", userResponseDTO);
    }

    /**
     * 사용자 정보 업데이트
     * @param request 인증된 사용자 요청 객체
     * @param userUpdateDTO 업데이트할 사용자 정보
     * @param imagefile 업데이트할 프로필 이미지 파일 (선택)
     * @return ResponseEntity<String> 업데이트 결과 메시지
     */
    @PatchMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateUserProfile(HttpServletRequest request,
                                               @RequestPart("userUpdateDTO") UserUpdateDTO userUpdateDTO,
                                               @RequestPart(value = "imagefile", required = false) MultipartFile imagefile) {

        System.out.println("imagefile controller:" + imagefile);

        // TODO : 3개 중 하나 성공하면 그냥 성공 때림 ㄱ
        userService.updateUserProfile(request, userUpdateDTO, imagefile);
        return ApiResponse.success("회원 정보가 수정되었습니다.", "User profile updated successfully", null);
    }

    /**
     * 닉네임 중복 확인
     * @param nickname 확인할 닉네임
     * @return ResponseEntity<String> 닉네임 사용 가능 여부 메시지
     */
    @GetMapping("/checkNickname")
    public ResponseEntity<?> checkNickname(@RequestParam("nickname") String nickname) {
        boolean isAvailable = userService.isNicknameAvailable(nickname);
        if (isAvailable) {
            return ApiResponse.success("사용 가능한 닉네임입니다.", "Nickname is available", null);
        } else {
            return ErrorResponse.error("중복된 닉네임입니다.", "Nickname is already taken", HttpStatus.CONFLICT, null);
        }
    }
}
