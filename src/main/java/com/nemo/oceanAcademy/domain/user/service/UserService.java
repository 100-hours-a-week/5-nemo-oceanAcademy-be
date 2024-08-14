package com.nemo.oceanAcademy.domain.user.service;

import com.nemo.oceanAcademy.domain.auth.service.AuthService;
import com.nemo.oceanAcademy.domain.user.entity.User;
import com.nemo.oceanAcademy.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthService oAuthService; // OAuth 관련 서비스

    @Autowired
    public UserService(UserRepository userRepository, AuthService oAuthService) {
        this.userRepository = userRepository;
        this.oAuthService = oAuthService;
    }

    // 현재 인증된 사용자의 ID 가져오기
    public Long getCurrentUserId() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.parseLong(userDetails.getUsername());
    }

    // 닉네임 중복 검사
    public boolean isNicknameTaken(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    // 회원가입 여부 확인
    public boolean isUserSignedUp(Long userId) {
        return userRepository.existsById(userId);
    }

    // 사용자 생성 (회원가입)
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // 사용자 삭제 (소프트 삭제)
    public void deleteCurrentUser() {
        Long userId = getCurrentUserId();

        // OAuth 로그아웃 또는 토큰 무효화 처리
        oAuthService.logoutUser(userId);

        // 애플리케이션 내에서 사용자 소프트 삭제
        userRepository.findById(userId).ifPresent(user -> {
            user.softDelete();
            userRepository.save(user);
        });
    }
}
