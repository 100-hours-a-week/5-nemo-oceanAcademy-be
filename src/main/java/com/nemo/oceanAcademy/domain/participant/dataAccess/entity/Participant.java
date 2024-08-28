package com.nemo.oceanAcademy.domain.participant.dataAccess.entity;
import com.nemo.oceanAcademy.domain.classroom.dataAccess.entity.Classroom;
import com.nemo.oceanAcademy.domain.user.dataAccess.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "participants")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Participant {

    //PK 사용자(수강생) 목록 식별자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //FK 강의실 아이디
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(nullable = false, name = "classroom_id")
    @NotNull(message = "Classroom must not be null")
    private Classroom classroom;

    ///FK 사용자(수강생) 아이디
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(nullable = false, name = "user_id")
    @NotNull(message = "User must not be null")
    private User user;

    //사용자(수강생) 수강 신청 시각
    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(ZoneId.of("Asia/Seoul")); // 한국 표준시(KST) 기준으로 생성 시간 설정
    }
}
