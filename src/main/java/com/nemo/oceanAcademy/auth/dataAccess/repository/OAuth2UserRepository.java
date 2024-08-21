package com.nemo.oceanAcademy.auth.dataAccess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nemo.oceanAcademy.domain.user.dataAccess.entity.User;

public interface OAuth2UserRepository extends JpaRepository<User, String> {
    boolean existsById(String id);
    boolean existsByNickname(String nickname);
}
