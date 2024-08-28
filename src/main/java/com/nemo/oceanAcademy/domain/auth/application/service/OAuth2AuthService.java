package com.nemo.oceanAcademy.domain.auth.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nemo.oceanAcademy.common.exception.ResourceNotFoundException;
import com.nemo.oceanAcademy.config.KakaoConfig;
import com.nemo.oceanAcademy.domain.user.dataAccess.entity.User;
import com.nemo.oceanAcademy.domain.user.dataAccess.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuth2AuthService {

    private final UserRepository userRepository;
    private final KakaoConfig kakaoConfig;

    // 카카오 API에서 액세스 토큰을 가져오기
    public String getKakaoAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        String tokenUrl = "https://kauth.kakao.com/oauth/token?grant_type=authorization_code" +
                "&client_id=" + kakaoConfig.getKakaoClientId() +
                "&redirect_uri=" + kakaoConfig.getRedirectUri() +
                "&code=" + code;
        String tokenResponse = restTemplate.postForObject(tokenUrl, null, String.class);
        return extractAccessToken(tokenResponse);
    }

    // 카카오 API에서 사용자 정보 가져오기
    public Map<String, Object> getKakaoUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        String url = userInfoUrl + "?access_token=" + accessToken;
        JsonNode userInfo = restTemplate.getForObject(url, JsonNode.class);

        if (userInfo == null || userInfo.path("id").isMissingNode()) {
            throw new ResourceNotFoundException("카카오 사용자 정보를 가져올 수 없습니다.", "Kakao user not found");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", userInfo.path("id").asText());
        return result;
    }

    // 회원가입 여부 확인
    public void checkSignup(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("해당 사용자가 존재하지 않습니다.", "User not found");
        }
    }

    // 회원가입 신청
    public void signup(String userId, String nickname, MultipartFile file) {
        if (userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("해당 ID로 이미 회원가입이 되어 있습니다.", "User already exists");
        }

        User user = new User();
        user.setId(userId);
        user.setNickname(nickname);
        user.setProfileImagePath(saveProfileImage(file));
        userRepository.save(user);
    }

    // 회원 탈퇴 처리 - soft delete
    public void withdraw(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 사용자를 찾을 수 없습니다.", "User not found"));
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // 액세스 토큰 추출
    private String extractAccessToken(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);
            return root.path("access_token").asText();
        } catch (IOException e) {
            throw new RuntimeException("카카오 액세스 토큰 추출에 실패했습니다.", e);
        }
    }

    // 프로필 이미지 저장 로직
    private String saveProfileImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            Path filePath = Paths.get("uploads/" + fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());
            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지 저장에 실패했습니다.", e);
        }
    }
}
