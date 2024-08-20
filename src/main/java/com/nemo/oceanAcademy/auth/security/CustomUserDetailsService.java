package com.nemo.oceanAcademy.auth.security;

import com.nemo.oceanAcademy.domain.user.dataAccess.entity.User;
import com.nemo.oceanAcademy.domain.user.dataAccess.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getId())
                .password("") // 비밀번호가 있다면 설정 (생략 시 비워두기)
                .authorities("USER") // 권한 설정
                .build();
    }
}
