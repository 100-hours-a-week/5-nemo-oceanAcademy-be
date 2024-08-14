package com.nemo.oceanAcademy.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    //PK 사용자 아이디, oauth 식별자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //사용자 이메일, 중복X, 선택적 등록
    @Column(unique = true, nullable = true, length = 40)
    private String email;

    // 사용자 닉네임, 중복X
    @Column(unique = true, length = 9, nullable = false)
    private String nickname;

    // 사용자 회원가입 시각
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // 사용자 회원탈퇴 시각 - soft delete
    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    // 사용자 프로필 사진 경로, 프로필 사진 선택적 등록
    @Column(columnDefinition = "TEXT", nullable = true)
    private String profileImagePath;
}
