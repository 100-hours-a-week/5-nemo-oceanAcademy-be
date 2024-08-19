package com.nemo.oceanAcademy.domain.user.repository;

import com.nemo.oceanAcademy.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByNickname(String nickname);
}
