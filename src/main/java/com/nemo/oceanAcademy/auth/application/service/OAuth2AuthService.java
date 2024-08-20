package com.nemo.oceanAcademy.auth.application.service;

import com.nemo.oceanAcademy.auth.application.dto.TokenResponseDTO;
import com.nemo.oceanAcademy.auth.security.JwtTokenProvider;
import com.nemo.oceanAcademy.domain.user.dataAccess.entity.User;
import com.nemo.oceanAcademy.domain.user.dataAccess.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OAuth2AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 파일 저장 경로
    private final String uploadDir = "src/main/resources/images"; // 경로는 환경 변수나 yml에서 설정하는 것이 좋음

    // 회원가입 여부 확인
    public boolean isUserSignedUp(String userId) {
        return userRepository.existsById(userId);
    }

    // 회원가입 처리 및 JWT 토큰 발급
    public TokenResponseDTO signupUser(String userId, String nickname, MultipartFile file) {
        // 이미 회원가입이 되어있는지 확인
        if (isUserSignedUp(userId)) {
            throw new IllegalArgumentException("이미 가입된 회원입니다.");
        }

        // 새로운 사용자 생성
        User newUser = User.builder()
                .id(userId)
                .nickname(nickname)
                .profileImagePath(file != null ? saveFileToDirectory(file) : null)
                .build();
        userRepository.save(newUser);

        // Access Token 및 Refresh Token 발급
        String accessToken = jwtTokenProvider.createAccessToken(userId);
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);

        return TokenResponseDTO.of(accessToken, refreshToken);
    }

    // 회원 탈퇴 (Soft Delete)
    public boolean softDeleteUser(String userId) {
        return userRepository.findById(userId).map(user -> {
            user.setDeletedAt(LocalDateTime.now());
            userRepository.save(user);
            return true;
        }).orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    }

    // 파일 저장 로직
    private String saveFileToDirectory(MultipartFile file) {
        try {
            // 고유한 파일 이름 생성
            String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);

            // 디렉토리 생성 (존재하지 않으면)
            Files.createDirectories(filePath.getParent());

            // 파일 저장
            Files.write(filePath, file.getBytes());

            // 저장된 파일 경로 반환
            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }
    }
}
