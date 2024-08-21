package com.nemo.oceanAcademy.domain.classroom.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClassroomResponseDto {

    // PK 강의실 아이디 (조회 시 반환)
    private Long id;

    // FK 사용자(강사) 아이디 (조회 시 반환)
    private String userId;

    // FK 카테고리 아이디 (조회 시 반환)
    private int categoryId;

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
}
