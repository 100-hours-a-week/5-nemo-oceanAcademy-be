package com.nemo.oceanAcademy.domain.user.application.service;
import com.nemo.oceanAcademy.common.exception.ResourceNotFoundException;
import com.nemo.oceanAcademy.domain.user.dataAccess.entity.User;
import com.nemo.oceanAcademy.domain.user.dataAccess.repository.UserRepository;
import com.nemo.oceanAcademy.domain.user.application.dto.UserCreateDTO;
import com.nemo.oceanAcademy.domain.user.application.dto.UserResponseDTO;
import com.nemo.oceanAcademy.domain.user.application.dto.UserUpdateDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.UUID;

/**
 * UserService는 사용자 정보 조회, 생성, 업데이트, 닉네임 중복 확인 등의 비즈니스 로직을 처리합니다.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 사용자 정보 조회
     * @param userId 사용자 ID
     * @return UserResponseDTO 사용자 정보 DTO
     * @throws ResourceNotFoundException 사용자 정보가 없을 경우 예외 발생
     */
    public UserResponseDTO getUserInfo(String userId) {
        return userRepository.findById(userId)
                .map(user -> new UserResponseDTO(user.getNickname(), user.getEmail(), user.getProfileImagePath()))
                .orElseThrow(() -> new ResourceNotFoundException("해당하는 ID(" + userId + ") 사용자를 찾을 수 없습니다.", "User not found"));
    }

    /**
     * 새로운 사용자 생성
     * @param userCreateDTO 사용자 생성 정보 DTO
     * @throws ResourceNotFoundException 이미 존재하는 사용자 ID일 경우 예외 발생
     */
    public void createUser(UserCreateDTO userCreateDTO) {
        if (userRepository.existsById(userCreateDTO.getUserId())) {
            throw new ResourceNotFoundException("해당 ID의 사용자가 이미 존재합니다.", "User already exists with ID: " + userCreateDTO.getUserId());
        }

        User user = User.builder()
                .id(userCreateDTO.getUserId()) // 카카오 UUID 사용
                .nickname(userCreateDTO.getNickname())
                .email(userCreateDTO.getEmail())
                .profileImagePath(userCreateDTO.getProfileImagePath())
                .build();

        userRepository.save(user);
    }

    /**
     * 사용자 정보 업데이트
     * @param request HttpServletRequest를 통해 userId 추출
     * @param userUpdateDTO 업데이트할 사용자 정보 DTO
     * @param imagefile 업데이트할 프로필 이미지 파일 (선택)
     * @throws ResourceNotFoundException 사용자 정보가 없을 경우 예외 발생
     */
    public void updateUserProfile(HttpServletRequest request, UserUpdateDTO userUpdateDTO, MultipartFile imagefile) {
        String userId = (String) request.getAttribute("userId");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다.", "User not found"));

        // 사용자 정보 업데이트
        if (userUpdateDTO.getNickname() != null) { user.setNickname(userUpdateDTO.getNickname()); }
        if (userUpdateDTO.getEmail() != null) { user.setEmail(userUpdateDTO.getEmail()); }

        // 프로필 이미지 파일 업데이트
        System.out.println("imagefile service:" + imagefile);
        if (imagefile != null && !imagefile.isEmpty()) {
            String fileName = saveFileToDirectory(imagefile);
            System.out.println("imagefile fileName:" + imagefile);
            user.setProfileImagePath(fileName);
        }

        userRepository.save(user); // 수정된 사용자 정보 저장
    }

    /**
     * 닉네임 중복 검사
     * @param nickname 확인할 닉네임
     * @return boolean 중복이 없으면 true 반환
     */
    public boolean isNicknameAvailable(String nickname) {
        return !userRepository.existsByNickname(nickname); // 닉네임 중복 확인
    }

    /**
     * 파일을 로컬 디렉토리에 저장
     * @param imagefile 저장할 파일
     * @return String 저장된 파일 경로
     * @throws RuntimeException 파일 저장 중 오류 발생 시 예외 처리
     */
    private String saveFileToDirectory(MultipartFile imagefile) {
        try {
            // 고유한 파일 이름 생성 (UUID + 원래 파일명)
            String fileName = UUID.randomUUID().toString() + "-" + imagefile.getOriginalFilename();
            Path imagefilePath = Paths.get(uploadDir + "/" + fileName);
            Files.createDirectories(imagefilePath.getParent());                  // 저장 경로가 없으면 생성
            Files.write(imagefilePath, imagefile.getBytes());                    // 파일 저장
            System.out.println("imagefilePath:" + imagefilePath);
            return fileName;                                                     // 저장된 파일 이름 반환
        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다.", e);                // 파일 저장 실패 시 예외 처리
        }
    }
}
