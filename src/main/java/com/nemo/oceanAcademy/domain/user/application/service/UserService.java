package com.nemo.oceanAcademy.domain.user.application.service;

import com.nemo.oceanAcademy.domain.user.dataAccess.entity.User;
import com.nemo.oceanAcademy.domain.user.dataAccess.repository.UserRepository;
import com.nemo.oceanAcademy.domain.user.application.dto.UserCreateDTO;
import com.nemo.oceanAcademy.domain.user.application.dto.UserResponseDTO;
import com.nemo.oceanAcademy.domain.user.application.dto.UserUpdateDTO;
import com.nemo.oceanAcademy.auth.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    public UserService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // JWT에서 추출한 UUID를 기반으로 사용자 정보 조회
    public UserResponseDTO getUserInfo(String token) {
        String userId = jwtTokenProvider.getUserIdFromToken(token); // JWT에서 사용자 ID 추출
        Optional<User> user = userRepository.findById(userId);
        return user.map(value -> new UserResponseDTO(value.getNickname(), value.getEmail(), value.getProfileImagePath()))
                .orElse(null); // 사용자가 없으면 null 반환
    }

    // 카카오 UUID 기반으로 사용자 생성
    public void createUser(UserCreateDTO userCreateDTO) {
        if (userRepository.existsById(userCreateDTO.getUserId())) {
            throw new RuntimeException("User already exists with ID: " + userCreateDTO.getUserId());
        }

        User user = User.builder()
                .id(userCreateDTO.getUserId()) // 카카오 UUID 사용
                .nickname(userCreateDTO.getNickname())
                .email(userCreateDTO.getEmail())
                .profileImagePath(userCreateDTO.getProfileImagePath())
                .build();

        userRepository.save(user);
    }

    // 사용자 정보 업데이트 (닉네임, 이메일, 프로필 이미지)
    public void updateUserProfile(String token, UserUpdateDTO userUpdateDTO, MultipartFile file) {
        String userId = jwtTokenProvider.getUserIdFromToken(token); // JWT에서 사용자 ID 추출
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // 닉네임 업데이트
            if (userUpdateDTO.getNickname() != null) {
                user.setNickname(userUpdateDTO.getNickname());
            }

            // 이메일 업데이트
            if (userUpdateDTO.getEmail() != null) {
                user.setEmail(userUpdateDTO.getEmail());
            }

            // 파일이 있으면 디렉토리에 업로드 후 경로 설정
            if (file != null && !file.isEmpty()) {
                String fileName = saveFileToDirectory(file);
                user.setProfileImagePath(fileName);
            }

            userRepository.save(user); // 수정된 사용자 정보 저장
        } else {
            throw new RuntimeException("User not found with ID: " + userId);
        }
    }

    // 닉네임 중복 검사
    public boolean isNicknameAvailable(String nickname) {
        return !userRepository.existsByNickname(nickname); // 닉네임 중복 확인
    }

    // 로컬 디렉토리에 파일 저장 로직
    private String saveFileToDirectory(MultipartFile file) {
        try {
            // 고유한 파일 이름 생성 (UUID + 원래 파일명)
            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + "/" + fileName);
            Files.createDirectories(filePath.getParent()); // 저장 경로가 없으면 생성
            Files.write(filePath, file.getBytes()); // 파일 저장
            return filePath.toString(); // 저장된 파일 경로 반환
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file to directory", e);
        }
    }
}
