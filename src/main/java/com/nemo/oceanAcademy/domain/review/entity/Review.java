package com.nemo.oceanAcademy.domain.review.entity;

import com.nemo.oceanAcademy.domain.classroom.entity.Classroom;
import com.nemo.oceanAcademy.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "reviews")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Review {

    //PK 리뷰 아이디, 식별자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //FK 강의실 아이디
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, name = "classroom_id")    // 데이터베이스 레벨에서 NOT NULL
    @NotNull(message = "Classroom must not be null")        // 애플리케이션 레벨에서 유효성 검사
    private Classroom classroom;

    //FK 사용자(수강자) 아이디
    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    @NotNull(message = "User must not be null")
    private User user;

    //강의 별점
    @Column(nullable = false)
    @Min(value = 1, message = "Rating should not be less than 1")
    @Max(value = 5, message = "Rating should not be greater than 5")
    @NotNull(message = "Rating must not be null")
    private Float rating;

    //리뷰 내용
    @Column(nullable = false, length = 700)
    @Size(min = 1, max = 500, message = "Content must be between 1 and 500 characters")
    @NotNull(message = "Content must not be null")
    private String content;

    //리뷰 생성 시각
    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));  // 한국 표준시(KST) 기준으로 생성 시간 설정
    }
}
