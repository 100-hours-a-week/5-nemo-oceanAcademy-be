package com.nemo.oceanAcademy.domain.classroom.application.dto;

import com.nemo.oceanAcademy.domain.classroom.dataAccess.entity.Classroom;
import jakarta.persistence.JoinColumn;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassroomResponseDto {

    // PK 강의실 아이디 (조회 시 반환)
    private Long id;

    // FK 사용자(강사) 아이디 (조회 시 반환)
    private String userId;

    // FK 카테고리 아이디 (조회 시 반환)
    private Integer categoryId;

    // 강사 닉네임 (조회 시 반환)
    @JoinColumn(name = "instructor_info")
    private String instructor;

    // 카테고리 이름 (조회 시 반환)
    @JoinColumn(name = "name")
    private String category;

    // 강의 이름 (조회 시 반환)
    private String name;

    // 강의 목표 (조회 시 반환)
    private String object;

    // 강의 소개 (조회 시 반환)
    private String description;

    // 강사 소개 (조회 시 반환)
    private String instructorInfo;

    // 강의 사전지식 및 준비물 (조회 시 반환)
    private String prerequisite;

    // 강의 공지 (조회 시 반환)
    private String announcement;

    // 강의 배너 이미지 경로 (조회 시 반환)
    private String bannerImagePath;

    // 강의실 활성화 여부 (조회 시 반환)
    private Boolean isActive;

    // 강의실 수강 인원
    private long studentCount;

    // Classroom 엔티티를 기반으로 생성하는 생성자 추가
    public ClassroomResponseDto(Classroom classroom) {
        this.id = classroom.getId();  // 강의실 아이디
        this.userId = classroom.getUser().getId();  // 강사 이름
        this.categoryId = classroom.getCategory().getId();  // 카테고리 이름
        this.instructor = classroom.getUser().getNickname();  // 강사 이름
        this.category = classroom.getCategory().getName();  // 카테고리 이름
        this.name = classroom.getName();  // 강의 이름
        this.object = classroom.getObject();  // 강의 목표
        this.description = classroom.getDescription();  // 강의 소개
        this.instructorInfo = classroom.getInstructorInfo();  // 강사 소개
        this.prerequisite = classroom.getPrerequisite();  // 사전 지식 및 준비물
        this.announcement = classroom.getAnnouncement();  // 강의 공지
        this.bannerImagePath = classroom.getBannerImagePath();  // 배너 이미지 경로
        this.isActive = classroom.getIsActive();  // 강의실 활성화 여부
    }

    // 수강 인원을 설정하는 생성자
    public ClassroomResponseDto(Classroom classroom, long studentCount) {
        this(classroom);
        this.studentCount = studentCount;
    }
}
