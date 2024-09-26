package com.nemo.oceanAcademy.domain.auth.application.service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nemo.oceanAcademy.common.exception.ResourceNotFoundException;
import com.nemo.oceanAcademy.common.exception.UserAlreadyExistsException;
import com.nemo.oceanAcademy.common.s3.S3ImageUtils;
import com.nemo.oceanAcademy.domain.auth.application.dto.SignupRequestDto;
import com.nemo.oceanAcademy.config.KakaoConfig;
import com.nemo.oceanAcademy.domain.user.dataAccess.entity.User;
import com.nemo.oceanAcademy.domain.user.dataAccess.repository.UserRepository;
import io.sentry.Sentry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@RequiredArgsConstructor
public class OAuth2AuthService {

    private final UserRepository userRepository;
    private final KakaoConfig kakaoConfig;
    private final S3ImageUtils imageUtils;

    // 카카오 API에서 액세스 토큰을 가져오기
    public String getKakaoAccessToken(String code, String environment) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String clientId = kakaoConfig.getKakaoClientId();

            // 환경에 따른 리다이렉트 URI 설정
            String redirectUri;
            switch (environment.toLowerCase()) {
                case "local":
                    redirectUri = kakaoConfig.getLocalRedirectUri();
                    break;
                case "dev":
                    redirectUri = kakaoConfig.getDevRedirectUri();
                    break;
                case "prod":
                    redirectUri = kakaoConfig.getServerRedirectUri();
                    break;
                default:
                    throw new IllegalArgumentException("잘못된 환경: " + environment);
            }

            // 로그로 확인
            System.out.println("환경: " + environment + ", 리다이렉트 URI: " + redirectUri);

            String tokenUrl = "https://kauth.kakao.com/oauth/token?grant_type=authorization_code" +
                    "&client_id=" + clientId +
                    "&redirect_uri=" + redirectUri +
                    "&code=" + code;

            ResponseEntity<String> tokenResponse = restTemplate.postForEntity(tokenUrl, null, String.class);
            return extractAccessToken(tokenResponse.getBody());
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new RuntimeException("카카오 액세스 토큰을 가져오는 중 오류가 발생했습니다.", e);
        }
    }

    // 액세스 토큰 추출 로직
    private String extractAccessToken(String responseBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);
            return root.path("access_token").asText();
        } catch (IOException e) {
            Sentry.captureException(e);
            throw new RuntimeException("엑세스 토큰 추출에 실패했습니다.", e);
        }
    }

    // 카카오 API에서 사용자 정보 가져오기
    public Map<String, Object> getKakaoUserInfo(String accessToken) {
        try {
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
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new RuntimeException("카카오 사용자 정보를 가져오는 중 오류가 발생했습니다.", e);
        }
    }

    /* --------------------------------------------------------------------------------------- */

    /**
     * 회원가입 여부 확인
     * @param userId 인증된 사용자 ID
     * @throws ResourceNotFoundException 사용자가 없을 경우 예외 발생
     */
    public void checkSignup(String userId) {
        try {
            if (!userRepository.existsById(userId)) {
                throw new ResourceNotFoundException("해당하는 ID(" + userId + ")의 사용자가 존재하지 않습니다.", "User not found");
            }
        } catch (ResourceNotFoundException e) {
            Sentry.captureException(e);
            throw e;
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new RuntimeException("회원가입 여부 확인 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 회원가입 신청
     * @param request 사용자 ID
     * @param signupRequestDto 사용자 닉네임
     * @param imagefile 프로필 이미지 파일 (선택)
     * @throws RuntimeException 이미 가입된 사용자가 있을 경우 예외 발생
     */
    public void signup(HttpServletRequest request, SignupRequestDto signupRequestDto, MultipartFile imagefile) {
        try {
            String userId = (String) request.getAttribute("userId");

            // 기존 유저가 존재하는지 확인
            if (userRepository.existsById(userId)) {
                throw new UserAlreadyExistsException("이미 가입된 사용자입니다.", "User already exists.");
            }

            // 새로운 User 객체 생성 및 정보 설정
            User user = new User();
            user.setId(userId);
            user.setNickname(signupRequestDto.getNickname());

            // 이메일 업데이트
            if (signupRequestDto.getEmail() != null) {
                user.setEmail(signupRequestDto.getEmail());
            }

            // 프로필 이미지 파일 업데이트
            if (imagefile != null && !imagefile.isEmpty()) {
                String fileName = imageUtils.saveFileToS3(imagefile);
                user.setProfileImagePath(fileName);
            }

            // 유저 정보 저장
            userRepository.save(user);
        } catch (UserAlreadyExistsException e) {
            Sentry.captureException(e);
            throw e;
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new RuntimeException("회원가입 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 회원탈퇴 처리 (soft delete)
     * @param userId 사용자 ID
     * @throws ResourceNotFoundException 존재하지 않는 사용자인 경우 예외 발생
     */
    public void withdraw(String userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("해당하는 ID(" + userId + ")의 사용자를 찾을 수 없습니다.", "User not found"));
            user.setDeletedAt(LocalDateTime.now());
            userRepository.save(user);
        } catch (ResourceNotFoundException e) {
            Sentry.captureException(e);
            throw e;
        } catch (Exception e) {
            Sentry.captureException(e);
            throw new RuntimeException("회원 탈퇴 처리 중 오류가 발생했습니다.", e);
        }
    }
}
