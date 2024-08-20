package com.nemo.oceanAcademy.auth.application.service;

import com.nemo.oceanAcademy.auth.application.dto.TokenResponseDTO;
import com.nemo.oceanAcademy.auth.domain.Token;
import com.nemo.oceanAcademy.auth.domain.repository.TokenRepository;
import com.nemo.oceanAcademy.auth.security.JwtTokenProvider;
import com.nemo.oceanAcademy.domain.user.dataAccess.entity.User;
import com.nemo.oceanAcademy.domain.user.dataAccess.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2AuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입 여부 확인
    public boolean isUserSignedUp(String userId) {
        return userRepository.existsById(userId);
    }

    // OAuth2 회원가입 처리 및 토큰 발급
    public TokenResponseDTO signupUser(String userId, String nickname, MultipartFile file) {
        if (userRepository.existsById(userId)) {
            throw new IllegalArgumentException("이미 가입된 회원입니다.");
        }

        User newUser = User.builder()
                .id(userId)
                .nickname(nickname)
                .profileImagePath(file != null ? saveFileToDirectory(file) : null)
                .build();
        userRepository.save(newUser);

        String accessToken = jwtTokenProvider.createAccessToken(userId);
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);

        return TokenResponseDTO.of(accessToken, refreshToken);
    }

    public boolean softDeleteUser(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setDeletedAt(LocalDateTime.now());
            userRepository.save(user);
            return true;
        }
        return false;
    }

    private String saveFileToDirectory(MultipartFile file) {
        // 파일 저장 로직 추가 가능
        return null;
    }
}
