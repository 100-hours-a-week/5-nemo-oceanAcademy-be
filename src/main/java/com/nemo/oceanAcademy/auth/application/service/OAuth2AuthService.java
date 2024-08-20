package com.nemo.oceanAcademy.auth.application.service;

import com.nemo.oceanAcademy.domain.user.dataAccess.entity.User;
import com.nemo.oceanAcademy.domain.user.dataAccess.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OAuth2AuthService {

    private final UserRepository userRepository;

    @Autowired
    public OAuth2AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 회원가입 여부 확인
    public boolean isUserSignedUp(String userId) {
        return userRepository.existsById(userId);
    }

    // OAuth2 회원가입 처리
    public boolean signupUser(String userId, String nickname, MultipartFile file) {
        if (userRepository.existsById(userId)) {
            return false;
        }
        User newUser = User.builder()
                .id(userId)
                .nickname(nickname)
                .profileImagePath(file != null ? uploadFileToS3(file) : null) // 프로필 사진 업로드 시 처리
                .build();
        userRepository.save(newUser);
        return true;
    }

    // Soft Delete (회원 탈퇴 처리)
    public boolean softDeleteUser(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setDeletedAt(LocalDateTime.now());  // 삭제 시각 설정 (soft delete)
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // S3 파일 업로드 메서드 (UserService에서 제공됨)
    private String uploadFileToS3(MultipartFile file) {
        // 실제 파일 업로드 처리 로직
        return "업로드된 파일 경로";
    }
}
