package com.nemo.oceanAcademy.domain.auth.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nemo.oceanAcademy.domain.auth.security.JwtTokenProvider;
import com.nemo.oceanAcademy.config.KakaoConfig;
import com.nemo.oceanAcademy.domain.user.dataAccess.entity.User;
import com.nemo.oceanAcademy.domain.user.dataAccess.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class OAuth2AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoConfig kakaoConfig;

    @Autowired
    public OAuth2AuthService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, KakaoConfig kakaoConfig) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.kakaoConfig = kakaoConfig;
    }

    // 카카오 API에서 액세스 토큰을 가져오는 메소드
    public String getKakaoAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        String clientId = kakaoConfig.getKakaoClientId();  // KakaoConfig에서 clientId 가져오기
        String redirectUri = kakaoConfig.getRedirectUri();  // KakaoConfig에서 redirectUri 가져오기
        String tokenUrl = "https://kauth.kakao.com/oauth/token?grant_type=authorization_code" +
                "&client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&code=" + code;

        ResponseEntity<String> tokenResponse = restTemplate.postForEntity(tokenUrl, null, String.class);
        return extractAccessToken(tokenResponse.getBody());
    }

    // 리다이렉트 처리 메소드
    public void redirectAfterLoginSuccess(HttpServletResponse response) {
        try {
            String successRedirectUri = "http://localhost:3000/success";  // 리다이렉트할 URI
            response.sendRedirect(successRedirectUri);
        } catch (IOException e) {
            throw new RuntimeException("Failed to redirect after login", e);
        }
    }

    // 카카오 API에서 사용자 정보를 가져오는 메소드
    public Map<String, Object> getKakaoUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                userInfoUrl, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class
        );
        JsonNode body = response.getBody();
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", body.path("id").asText());
        userInfo.put("email", body.path("kakao_account").path("email").asText());

        return userInfo;
    }

    // 회원가입 여부 확인
    public ResponseEntity<?> checkSignup(String userId) {
        System.out.println("userId :" + userId);
        if (userRepository.existsById(userId)) {
            return ResponseEntity.ok("{\"message\": \"이미 가입된 회원입니다.\"}");
        } else {
            return ResponseEntity.status(204).body("{\"message\": \"회원 기록이 없습니다\"}");
        }
    }

    // 회원가입 신청
    public ResponseEntity<?> signup(String userId, String nickname, MultipartFile file) {
        if (userRepository.existsById(userId)) {
            return ResponseEntity.status(400).body("{\"message\": \"이미 가입된 회원입니다.\"}");
        }

        // 파일 저장 로직 및 유저 생성
        String profileImagePath = saveProfileImage(file);

        User user = new User();
        user.setId(userId);
        user.setNickname(nickname);
        user.setProfileImagePath(profileImagePath);
        userRepository.save(user);

        return ResponseEntity.status(201).body("{\"message\": \"회원가입이 완료되었습니다.\"}");
    }

    // 회원 탈퇴 처리
    public ResponseEntity<?> withdraw(String userId) {
        if (userRepository.existsById(userId)) {
            User user = userRepository.findById(userId).get();
            user.setDeletedAt(LocalDateTime.now());  // 삭제 일시 기록
            userRepository.save(user);
            return ResponseEntity.ok("{\"message\": \"회원탈퇴가 되었습니다.\"}");
        } else {
            return ResponseEntity.status(400).body("{\"message\": \"회원탈퇴에 실패했습니다.\"}");
        }
    }

    // 액세스 토큰 추출 로직
    private String extractAccessToken(String responseBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);
            return root.path("access_token").asText();
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract access token", e);
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
            throw new RuntimeException("Failed to save profile image", e);
        }
    }
}
