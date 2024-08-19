package com.nemo.oceanAcademy.domain.user.entity;

import com.nemo.oceanAcademy.domain.classroom.entity.Classroom;
import com.nemo.oceanAcademy.domain.participant.entity.Participant;
import com.nemo.oceanAcademy.domain.review.entity.Review;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class User {

    //PK 사용자 아이디, oauth 식별자, UUID 생성
    @Id
    private String id;

    //사용자 이메일, 중복X, 선택적 등록
    @Column(nullable = true, unique = true, length = 400)
    private String email;

    // 사용자 닉네임, 중복X
    @Column(nullable = false, unique = true, length = 100)
    @NotNull(message = "Nickname is mandatory")
    @Size(min = 2, max = 9, message = "Nickname must be between 2 and 9 characters")
    private String nickname;

    // 사용자 프로필 사진 경로, 프로필 사진 선택적 등록
    @Column(length = 300)
    private String profileImagePath;

    // 사용자 회원가입 시각
    @Column(nullable = false, name = "created_at")
    @NotNull(message = "Creation time must not be null")
    private LocalDateTime createdAt;

    // 사용자 회원탈퇴 시각 - soft delete
    @Column(nullable = true, name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(ZoneId.of("Asia/Seoul")); // 한국 표준시(KST) 기준으로 생성 시간 설정
        if (this.id == null) {
            this.id = UUID.randomUUID().toString(); // UUID 생성
        }
    }

    //양방향 관계 = users과 연관된 테이블 3개
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Review> reviews;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Classroom> classrooms;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Participant> participants;
}
