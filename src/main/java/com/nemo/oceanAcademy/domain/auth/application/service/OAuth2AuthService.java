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
    private final KakaoConfig kakaoConfig;

    @Autowired
    public OAuth2AuthService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, KakaoConfig kakaoConfig) {
        this.userRepository = userRepository;
        this.kakaoConfig = kakaoConfig;
    }

    // 카카오 API에서 액세스 토큰을 가져오기
    public String getKakaoAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        String clientId = kakaoConfig.getKakaoClientId();
        String redirectUri = kakaoConfig.getRedirectUri();
        String tokenUrl = "https://kauth.kakao.com/oauth/token?grant_type=authorization_code" +
                          "&client_id=" + clientId +
                          "&redirect_uri=" + redirectUri +
                          "&code=" + code;
        ResponseEntity<String> tokenResponse = restTemplate.postForEntity(tokenUrl, null, String.class);
        return extractAccessToken(tokenResponse.getBody());
    }

    // 리다이렉션
    public void redirectAfterLoginSuccess(HttpServletResponse response) {
        try {
            String successRedirectUri = "http://localhost:3000/success";
            response.sendRedirect(successRedirectUri);
        } catch (IOException e) {
            throw new RuntimeException("로그인 성공, 리다이렉트 안됨", e);
        }
    }

    // 카카오 API에서 사용자 정보 가져오기
    public Map<String, Object> getKakaoUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        // 요청 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        ResponseEntity<JsonNode> response = restTemplate.exchange(
                userInfoUrl, HttpMethod.GET, new HttpEntity<>(headers), JsonNode.class
        );
        JsonNode body = response.getBody();

        // 사용자 식별자 값 가져오기
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", body.path("id").asText());

        return userInfo;
    }

    // 회원가입 여부 확인
    public ResponseEntity<?> checkSignup(String userId) {
        // userId로 사용자 검색
        if (userRepository.existsById(userId)) {
            return ResponseEntity.ok("{\"message\": \"기존 회원임\"}");
        } else {
            return ResponseEntity.status(204).body("{\"message\": \"새로운 회원임\"}");
        }
    }

    // 회원가입 신청
    public ResponseEntity<?> signup(String userId, String nickname, MultipartFile file) {
        // userId로 사용자 검색
        if (userRepository.existsById(userId)) {
            return ResponseEntity.status(400).body("{\"message\": \"기존 회원임\"}");
        }

        // id, 닉네임, 프로필 이미지(선택) 사용자 등록
        User user = new User();
        user.setId(userId);
        user.setNickname(nickname);
        String profileImagePath = saveProfileImage(file); // 선택
        user.setProfileImagePath(profileImagePath);
        userRepository.save(user);

        return ResponseEntity.status(201).body("{\"message\": \"회원가입 완료\"}");
    }

    // 회원 탈퇴 처리 - soft delete
    public ResponseEntity<?> withdraw(String userId) {
        if (userRepository.existsById(userId)) {
            // userId로 사용자 검색
            User user = userRepository.findById(userId).get();

            // deleted_at 컬럼에 삭제 일시 기록
            user.setDeletedAt(LocalDateTime.now());
            userRepository.save(user);
            return ResponseEntity.ok("{\"message\": \"회원탈퇴 완료\"}");
        } else {
            return ResponseEntity.status(400).body("{\"message\": \"회원탈퇴 실패\"}");
        }
    }

    //  ------------------------------------------------------------------------- //

    // 액세스 토큰 추출 로직
    private String extractAccessToken(String responseBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);
            return root.path("access_token").asText();
        } catch (IOException e) {
            throw new RuntimeException("엑세스 토큰 추출에 실패", e);
        }
    }

    // 프로필 이미지 저장 로직
    private String saveProfileImage(MultipartFile file) {
        // 등록된 파일이 없다면 빈 경로 반환
        if (file == null || file.isEmpty()) {
            return null;
        }
        // 등록된 파일이 있다면 프로필 경로 반환
        try {
            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            Path filePath = Paths.get("uploads/" + fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());
            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지 저장에 실패", e);
        }
    }
}
