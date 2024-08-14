package com.nemo.oceanAcademy.domain.clazz.entity;

import com.nemo.oceanAcademy.domain.category.entity.Category;
import com.nemo.oceanAcademy.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "classes")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Clazz {

    //PK 강의실 아이디, 식별자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //FK 사용자(강사) 아이디
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //FK 카테고리 아이디
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    //강의 이름
    @Column(length = 24, nullable = false)
    private String name;

    //강의 목표
    @Column(columnDefinition = "TEXT", nullable = false)
    private String object;

    //강의 소개
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    //강사 소개, 선택적 작성
    @Column(columnDefinition = "TEXT", nullable = true)
    private String instructorInfo;

    //강의 사전지식 및 준비물, 선택적 작성
    @Column(columnDefinition = "TEXT", nullable = true)
    private String prerequisite;

    //강의실 배너 사진 경로, 선택적 작성
    @Column(name = "banner_image_path", columnDefinition = "TEXT")
    private String bannerImagePath;

    //강의실 공지
    @Column(columnDefinition = "TEXT", nullable = true)
    private String announcement;

    //강의실 라이브 강의 활성화 여부
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    //강의실 생성 시각
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    //강의실 삭제 시각 - soft delete
    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;
    public void softDelete() { this.deletedAt = LocalDateTime.now(); }
}
