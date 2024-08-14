package com.nemo.oceanAcademy.domain.review.entity;

import com.nemo.oceanAcademy.domain.clazz.entity.Clazz;
import com.nemo.oceanAcademy.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private Clazz clazz;

    //FK 사용자(수강자) 아이디
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //강의 별점
    private Float rating;

    //리뷰 생성 시각
    @Column(name = "created_at", nullable = true)
    private LocalDateTime createdAt;
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    //리뷰 내용
    @Column(columnDefinition = "TEXT")
    private String content;
}
