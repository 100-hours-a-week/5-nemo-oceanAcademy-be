package com.nemo.oceanAcademy.domain.user.dataAccess.repository;

import com.nemo.oceanAcademy.domain.user.dataAccess.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsById(String id);
    boolean existsByNickname(String nickname);
}
