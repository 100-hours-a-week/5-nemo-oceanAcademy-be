package com.nemo.oceanAcademy.domain.classroom.dataAccess.entity;

import com.nemo.oceanAcademy.domain.category.dataAccess.entity.Category;
import com.nemo.oceanAcademy.domain.participant.dataAccess.entity.Participant;
import com.nemo.oceanAcademy.domain.review.dataAccess.entity.Review;
import com.nemo.oceanAcademy.domain.schedule.dataAccess.entity.Schedule;
import com.nemo.oceanAcademy.domain.user.dataAccess.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Entity
@Table(name = "classrooms")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE classrooms SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Classroom {

    //PK 강의실 아이디, 식별자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //FK 사용자(강사) 아이디
    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    @NotNull(message = "User must not be null")
    private User user;

    //FK 카테고리 아이디
    @ManyToOne
    @JoinColumn(nullable = false, name = "category_id")
    @NotNull(message = "Category must not be null")
    private Category category;

    //강의 이름
    @Column(nullable = false, length = 100)
    @Size(min = 2, max = 24, message = "Name must be between 2 and 24 characters")
    @NotNull(message = "Name must not be null")
    private String name;

    //강의 목표
    @Column(nullable = false, length = 700)
    @Size(min = 1, max = 500, message = "Object must be between 1 and 500 characters")
    @NotNull(message = "Object must not be null")
    private String object;

    //강의 소개
    @Column(nullable = false, length = 700)
    @Size(min = 1, max = 500, message = "Description must be between 1 and 500 characters")
    @NotNull(message = "Description must not be null")
    private String description;

    //강사 소개, 선택적 작성
    @Column(nullable = true, length = 700)
    @Size(min = 1, max = 500, message = "Instructor info must be between 1 and 500 characters")
    private String instructorInfo;

    //강의 사전지식 및 준비물, 선택적 작성
    @Column(nullable = true, length = 700)
    @Size(min = 1, max = 500, message = "Prerequisite must be between 1 and 500 characters")
    private String prerequisite;

    //강의실 공지
    @Column(nullable = true, length = 700)
    @Size(min = 1, max = 500, message = "Announcement must be between 1 and 500 characters")
    private String announcement;

    //강의실 배너 사진 경로, 선택적 작성
    @Column(nullable = true, length = 300, name = "banner_image_path")
    private String bannerImagePath;

    //강의실 라이브 강의 활성화 여부
    @Column(nullable = false, name = "is_active")
    private Boolean isActive;

    //강의실 생성 시각
    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now(ZoneId.of("Asia/Seoul"));  // 한국 표준시(KST) 기준으로 생성 시간 설정
    }

    //강의실 삭제 시각 - soft delete
    @Column(nullable = true, name = "deleted_at")
    private LocalDateTime deletedAt;

    @PreRemove
    public void preRemove() {
        this.deletedAt = LocalDateTime.now(); // 삭제 시각 설정
    }

    //양방향 관계 = classrooms과 연관된 테이블 3개
    @OneToMany(mappedBy = "classroom", fetch = FetchType.LAZY)
    private List<Review> reviews;

    @OneToMany(mappedBy = "classroom", fetch = FetchType.LAZY)
    private List<Schedule> schedules;

    @OneToMany(mappedBy = "classroom", fetch = FetchType.LAZY)
    private List<Participant> participants;
}
