package com.nemo.oceanAcademy.domain.auth.application.service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nemo.oceanAcademy.common.exception.ResourceNotFoundException;
import com.nemo.oceanAcademy.common.exception.UserAlreadyExistsException;
import com.nemo.oceanAcademy.domain.auth.security.JwtTokenProvider;
import com.nemo.oceanAcademy.config.KakaoConfig;
import com.nemo.oceanAcademy.domain.user.dataAccess.entity.User;
import com.nemo.oceanAcademy.domain.user.dataAccess.repository.UserRepository;
import io.sentry.Sentry;
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
    public OAuth2AuthService(UserRepository userRepository, KakaoConfig kakaoConfig) {
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

    // 액세스 토큰 추출 로직
    private String extractAccessToken(String responseBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);
            return root.path("access_token").asText();
        } catch (IOException e) {
            Sentry.captureException(e);
            throw new RuntimeException("엑세스 토큰 추출에 실패", e);
        }
    }

    // 리다이렉션
    public void redirectAfterLoginSuccess(HttpServletResponse response) {
        try {
            String successRedirectUri = kakaoConfig.getRedirectUri();
            response.sendRedirect(successRedirectUri);
        } catch (IOException e) {
            Sentry.captureException(e);
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

    /* --------------------------------------------------------------------------------------- */

    /**
     * 회원가입 여부 확인
     * @param userId 인증된 사용자 ID
     * @throws ResourceNotFoundException 사용자가 없을 경우 예외 발생
     */
    public void checkSignup(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("해당하는 ID(" + userId + ")의 사용자가 존재하지 않습니다.", "User not found");
        }
    }

    /**
     * 회원가입 신청
     * @param userId 사용자 ID
     * @param nickname 사용자 닉네임
     * @param file 프로필 이미지 파일 (선택)
     * @throws RuntimeException 이미 가입된 사용자가 있을 경우 예외 발생
     */
    public void signup(String userId, String nickname, MultipartFile file) {
        if (userRepository.existsById(userId)) {
            throw new UserAlreadyExistsException(
                    "이미 가입된 사용자입니다.", // 한국어 메시지
                    "User already exists."      // 영어 메시지
            );
        }

        User user = new User();
        user.setId(userId);
        user.setNickname(nickname);
        user.setProfileImagePath(saveProfileImage(file));  // 프로필 이미지 저장
        userRepository.save(user);
    }


    /**
     * 회원탈퇴 처리 (soft delete)
     * @param userId 사용자 ID
     * @throws ResourceNotFoundException 존재하지 않는 사용자인 경우 예외 발생
     */
    public void withdraw(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("해당하는 ID(" + userId + ")의 사용자를 찾을 수 없습니다.", "User not found"));
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * 프로필 이미지 저장 로직
     * @param file 저장할 파일
     * @return 저장된 파일 경로 또는 null
     */
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
        } catch (Exception e) {
            throw new RuntimeException("프로필 이미지 저장에 실패했습니다.", e);
        }
    }
}
